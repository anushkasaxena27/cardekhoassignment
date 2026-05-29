package com.cardekho.dto.recommendation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ExtractedIntentDto(
		BigDecimal budgetMinInr,
		BigDecimal budgetMaxInr,
		Set<String> bodyTypes,
		Set<String> fuelTypes,
		Set<String> usageIntents,
		Set<String> featureSignals,
		Set<String> sentimentSignals,
		String transmissionPreference) {
}
