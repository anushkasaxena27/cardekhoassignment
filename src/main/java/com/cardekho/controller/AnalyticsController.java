package com.cardekho.controller;

import com.cardekho.dto.car.CarSummaryDto;
import com.cardekho.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	@GetMapping("/trending")
	@Operation(summary = "Trending cars by engagement")
	public List<CarSummaryDto> trending() {
		return analyticsService.trending();
	}

	@GetMapping("/most-searched")
	@Operation(summary = "Most searched cars")
	public List<CarSummaryDto> mostSearched() {
		return analyticsService.mostSearched();
	}

	@GetMapping("/top-rated")
	@Operation(summary = "Top rated by safety score")
	public List<CarSummaryDto> topRated() {
		return analyticsService.topRated();
	}
}
