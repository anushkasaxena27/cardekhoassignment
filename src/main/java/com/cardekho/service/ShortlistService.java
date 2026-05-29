package com.cardekho.service;

import com.cardekho.dto.car.CarSummaryDto;
import com.cardekho.entity.AppUser;
import com.cardekho.entity.UserShortlist;
import com.cardekho.exception.BadRequestException;
import com.cardekho.mapper.CarMapper;
import com.cardekho.repository.AppUserRepository;
import com.cardekho.repository.CarVariantRepository;
import com.cardekho.repository.UserShortlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShortlistService {

	private final UserShortlistRepository userShortlistRepository;
	private final AppUserRepository appUserRepository;
	private final CarVariantRepository carVariantRepository;
	private final CarMapper carMapper;

	@Transactional(readOnly = true)
	public List<CarSummaryDto> listMine() {
		AppUser user = currentUser();
		return userShortlistRepository.findDetailedByUserId(user.getId()).stream()
				.map(UserShortlist::getVariant)
				.map(carMapper::toSummary)
				.toList();
	}

	@Transactional
	public void add(Long variantId) {
		AppUser user = currentUser();
		if (userShortlistRepository.findByUser_IdAndVariant_Id(user.getId(), variantId).isPresent()) {
			return;
		}
		UserShortlist row = UserShortlist.builder()
				.user(user)
				.variant(carVariantRepository.getReferenceById(variantId))
				.build();
		userShortlistRepository.save(row);
	}

	@Transactional
	public void remove(Long variantId) {
		AppUser user = currentUser();
		userShortlistRepository.deleteByUser_IdAndVariant_Id(user.getId(), variantId);
	}

	private AppUser currentUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return appUserRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new BadRequestException("User not resolved"));
	}
}
