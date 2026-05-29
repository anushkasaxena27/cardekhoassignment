package com.cardekho.dto.car;

import java.math.BigDecimal;

public record SafetyRatingDto(String agency, BigDecimal rating, Integer airbags, Boolean abs, Boolean esc,
		String adasFeatures) {
}
