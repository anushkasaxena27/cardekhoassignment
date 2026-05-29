package com.cardekho.controller;

import com.cardekho.dto.recommendation.ChatRecommendationRequest;
import com.cardekho.dto.recommendation.ChatRecommendationResponse;
import com.cardekho.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

	private final RecommendationService recommendationService;

	@PostMapping("/chat")
	@Operation(summary = "Conversational recommendations from natural language")
	public ChatRecommendationResponse chat(@Valid @RequestBody ChatRecommendationRequest request) {
		return recommendationService.chat(request);
	}
}
