package com.cardekho.dto.car;

import java.math.BigDecimal;

public record CarSummaryDto(
		Long id,
		String makeName,
		String modelName,
		String variantName,
		String bodyType,
		String segment,
		String fuelType,
		String transmission,
		BigDecimal exShowroomPrice,
		BigDecimal onRoadPrice,
		BigDecimal mileage,
		String imageUrl,
		String availabilityStatus,
		Boolean sunroof,
		Boolean panoramicSunroof) {
}
