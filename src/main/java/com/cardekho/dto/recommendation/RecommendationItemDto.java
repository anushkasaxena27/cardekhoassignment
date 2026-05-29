package com.cardekho.dto.recommendation;

import java.math.BigDecimal;
import java.util.List;

public record RecommendationItemDto(
		Long variantId,
		String car,
		int matchScore,
		List<String> reasons,
		String imageUrl,
		BigDecimal exShowroomPrice,
		BigDecimal mileage) {
}
