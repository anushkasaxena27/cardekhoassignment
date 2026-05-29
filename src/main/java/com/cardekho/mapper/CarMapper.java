package com.cardekho.mapper;

import com.cardekho.dto.car.*;
import com.cardekho.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CarMapper {

	@Mapping(target = "makeName", source = "model.make.name")
	@Mapping(target = "modelName", source = "model.modelName")
	@Mapping(target = "sunroof", source = "features", qualifiedByName = "mapSunroof")
	@Mapping(target = "panoramicSunroof", source = "features", qualifiedByName = "mapPanoramic")
	public abstract CarSummaryDto toSummary(CarVariant variant);

	public CarDetailDto toDetail(CarVariant variant, BigDecimal averageReview, long reviewCount) {
		List<SafetyRatingDto> safety = variant.getSafetyRatings().stream()
				.sorted(Comparator.comparing(SafetyRating::getAgency))
				.map(this::toDto)
				.toList();
		CarFeatureDto features = variant.getFeatures() == null ? emptyFeatures() : toDto(variant.getFeatures());
		List<CarImageDto> images = variant.getImages().stream().map(this::toDto).toList();
		return new CarDetailDto(
				variant.getId(),
				variant.getModel().getMake().getName(),
				variant.getModel().getModelName(),
				variant.getVariantName(),
				variant.getModel().getBodyType(),
				variant.getModel().getSegment(),
				variant.getModel().getLaunchYear(),
				variant.getFuelType(),
				variant.getTransmission(),
				variant.getSeatingCapacity(),
				variant.getEngineCc(),
				variant.getHorsepower(),
				variant.getTorque(),
				variant.getMileage(),
				variant.getCityMileage(),
				variant.getHighwayMileage(),
				variant.getBootSpace(),
				variant.getGroundClearance(),
				variant.getDrivetrain(),
				variant.getExShowroomPrice(),
				variant.getOnRoadPrice(),
				variant.getImageUrl(),
				variant.getAvailabilityStatus().name(),
				safety,
				features,
				images,
				averageReview,
				reviewCount);
	}

	public abstract SafetyRatingDto toDto(SafetyRating rating);

	public abstract CarFeatureDto toDto(CarFeature feature);

	public abstract CarImageDto toDto(CarImage image);

	private static CarFeatureDto emptyFeatures() {
		return new CarFeatureDto(false, false, false, null, false, false, false, null);
	}

	@Named("mapSunroof")
	Boolean mapSunroof(CarFeature f) {
		return f != null ? f.getSunroof() : Boolean.FALSE;
	}

	@Named("mapPanoramic")
	Boolean mapPanoramic(CarFeature f) {
		return f != null ? f.getPanoramicSunroof() : Boolean.FALSE;
	}
}
