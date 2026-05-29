package com.cardekho.controller;

import com.cardekho.dto.car.*;
import com.cardekho.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

	private final CarService carService;

	@GetMapping
	@Operation(summary = "List cars (paginated)")
	public Page<CarSummaryDto> list(@PageableDefault(size = 20) Pageable pageable) {
		return carService.list(pageable);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get car variant details")
	public CarDetailDto detail(@PathVariable Long id) {
		return carService.getDetailAndTrackView(id);
	}

	@PostMapping("/filter")
	@Operation(summary = "Filter cars by structured criteria")
	public Page<CarSummaryDto> filter(@RequestBody(required = false) CarFilterRequest filter,
			@PageableDefault(size = 20) Pageable pageable) {
		CarFilterRequest f = filter == null ? new CarFilterRequest(null, null, null, null, null, null, null) : filter;
		return carService.filter(f, pageable);
	}

	@PostMapping("/search")
	@Operation(summary = "Search cars by free text")
	public Page<CarSummaryDto> search(@Valid @RequestBody TextSearchRequest request,
			@PageableDefault(size = 20) Pageable pageable) {
		return carService.search(request.q(), pageable);
	}

	@PostMapping("/compare")
	@Operation(summary = "Compare up to four variants side by side")
	public List<CarDetailDto> compare(@Valid @RequestBody CarCompareRequest request) {
		return carService.compare(request.variantIds());
	}
}
