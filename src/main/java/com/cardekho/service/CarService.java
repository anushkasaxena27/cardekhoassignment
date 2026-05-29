package com.cardekho.service;

import com.cardekho.dto.car.*;
import com.cardekho.entity.CarVariant;
import com.cardekho.exception.NotFoundException;
import com.cardekho.mapper.CarMapper;
import com.cardekho.repository.CarVariantRepository;
import com.cardekho.repository.UserReviewRepository;
import com.cardekho.repository.spec.CarVariantSpecifications;
import com.cardekho.util.JpaScalars;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

	private final CarVariantRepository carVariantRepository;
	private final UserReviewRepository userReviewRepository;
	private final CarMapper carMapper;
	private final SearchLogService searchLogService;

	@Transactional(readOnly = true)
	public Page<CarSummaryDto> list(Pageable pageable) {
		return carVariantRepository.findAll(pageable).map(carMapper::toSummary);
	}

	@Transactional(readOnly = true)
	public CarDetailDto getDetail(Long id) {
		CarVariant v = carVariantRepository.findDetailById(id)
				.orElseThrow(() -> new NotFoundException("Car variant not found"));
		BigDecimal avg = JpaScalars.bigDecimal(userReviewRepository.averageRatingForVariant(id));
		long cnt = userReviewRepository.countByVariantId(id);
		return carMapper.toDetail(v, avg, cnt);
	}

	@Transactional
	public CarDetailDto getDetailAndTrackView(Long id) {
		carVariantRepository.incrementViewCount(id);
		return getDetail(id);
	}

	@Transactional(readOnly = true)
	public Page<CarSummaryDto> filter(CarFilterRequest filter, Pageable pageable) {
		Specification<CarVariant> spec = CarVariantSpecifications.withFilters(
				filter.makeId(),
				filter.bodyType(),
				filter.fuelType(),
				filter.transmission(),
				filter.minPrice(),
				filter.maxPrice(),
				filter.segment());
		return carVariantRepository.findAll(spec, pageable).map(carMapper::toSummary);
	}

	@Transactional
	public Page<CarSummaryDto> search(String q, Pageable pageable) {
		searchLogService.logQuery(q, null);
		Specification<CarVariant> spec = CarVariantSpecifications.searchText(q);
		Page<CarSummaryDto> page = carVariantRepository.findAll(spec, pageable).map(carMapper::toSummary);
		page.getContent().forEach(dto -> carVariantRepository.incrementSearchCount(dto.id()));
		return page;
	}

	@Transactional(readOnly = true)
	public List<CarDetailDto> compare(List<Long> variantIds) {
		List<Long> ids = variantIds.stream().distinct().sorted().toList();
		Map<Long, BigDecimal> avgs = userReviewRepository.averageRatingsByVariantIds(ids).stream()
				.collect(Collectors.toMap(row -> (Long) row[0], row -> JpaScalars.bigDecimal(row[1])));
		Map<Long, Long> counts = ids.stream().collect(Collectors.toMap(id -> id, userReviewRepository::countByVariantId));
		return ids.stream()
				.map(id -> carVariantRepository.findDetailById(id)
						.orElseThrow(() -> new NotFoundException("Variant not found: " + id)))
				.sorted(Comparator.comparing(CarVariant::getId))
				.map(v -> carMapper.toDetail(v, avgs.getOrDefault(v.getId(), BigDecimal.ZERO), counts.getOrDefault(v.getId(), 0L)))
				.toList();
	}
}
