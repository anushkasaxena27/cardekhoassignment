package com.cardekho.dto.car;

import java.math.BigDecimal;

public record CarFilterRequest(
		Long makeId,
		String bodyType,
		String fuelType,
		String transmission,
		BigDecimal minPrice,
		BigDecimal maxPrice,
		String segment) {
}
