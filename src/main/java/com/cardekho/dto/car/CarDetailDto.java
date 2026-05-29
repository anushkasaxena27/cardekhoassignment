package com.cardekho.dto.car;

import java.math.BigDecimal;
import java.util.List;

public record CarDetailDto(
		Long id,
		String makeName,
		String modelName,
		String variantName,
		String bodyType,
		String segment,
		Integer launchYear,
		String fuelType,
		String transmission,
		Integer seatingCapacity,
		Integer engineCc,
		Integer horsepower,
		Integer torque,
		BigDecimal mileage,
		BigDecimal cityMileage,
		BigDecimal highwayMileage,
		Integer bootSpace,
		Integer groundClearance,
		String drivetrain,
		BigDecimal exShowroomPrice,
		BigDecimal onRoadPrice,
		String imageUrl,
		String availabilityStatus,
		List<SafetyRatingDto> safetyRatings,
		CarFeatureDto features,
		List<CarImageDto> images,
		BigDecimal averageUserRating,
		long reviewCount) {
}
