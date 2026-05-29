package com.cardekho.dto.recommendation;

import com.cardekho.dto.car.CarFilterRequest;

import java.util.List;

public record ChatRecommendationResponse(
		ExtractedIntentDto extractedIntent,
		CarFilterRequest appliedFilters,
		List<RecommendationItemDto> recommendations) {
}
