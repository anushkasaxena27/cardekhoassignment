package com.cardekho.service;

import com.cardekho.dto.car.CarSummaryDto;
import com.cardekho.mapper.CarMapper;
import com.cardekho.repository.CarVariantRepository;
import com.cardekho.repository.SafetyRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

	public static final String CACHE_TRENDING = "trendingCars";

	private final CarVariantRepository carVariantRepository;
	private final SafetyRatingRepository safetyRatingRepository;
	private final CarMapper carMapper;

	@Cacheable(cacheNames = CACHE_TRENDING)
	@Transactional(readOnly = true)
	public List<CarSummaryDto> trending() {
		return carVariantRepository.findTopTrending(PageRequest.of(0, 10)).stream().map(carMapper::toSummary).toList();
	}

	@Transactional(readOnly = true)
	public List<CarSummaryDto> mostSearched() {
		return carVariantRepository.findTop10ByOrderBySearchCountDesc().stream().map(carMapper::toSummary).toList();
	}

	@Transactional(readOnly = true)
	public List<CarSummaryDto> topRated() {
		return safetyRatingRepository.findTopRated(PageRequest.of(0, 10)).stream()
				.map(sr -> carMapper.toSummary(sr.getVariant()))
				.toList();
	}
}
