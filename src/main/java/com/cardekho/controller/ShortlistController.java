package com.cardekho.controller;

import com.cardekho.dto.car.CarSummaryDto;
import com.cardekho.service.ShortlistService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shortlist")
@RequiredArgsConstructor
public class ShortlistController {

	private final ShortlistService shortlistService;

	@GetMapping
	@Operation(summary = "List my shortlisted cars")
	public List<CarSummaryDto> list() {
		return shortlistService.listMine();
	}

	@PostMapping("/{variantId}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Add variant to shortlist")
	public void add(@PathVariable Long variantId) {
		shortlistService.add(variantId);
	}

	@DeleteMapping("/{variantId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Remove variant from shortlist")
	public void remove(@PathVariable Long variantId) {
		shortlistService.remove(variantId);
	}
}
