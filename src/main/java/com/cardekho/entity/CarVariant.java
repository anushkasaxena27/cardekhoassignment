package com.cardekho.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_variant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarVariant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "model_id", nullable = false)
	private CarModel model;

	@Column(name = "variant_name", nullable = false)
	private String variantName;

	@Column(name = "fuel_type", nullable = false, length = 40)
	private String fuelType;

	@Column(nullable = false, length = 40)
	private String transmission;

	@Column(name = "seating_capacity", nullable = false)
	private Integer seatingCapacity;

	@Column(name = "engine_cc")
	private Integer engineCc;

	private Integer horsepower;

	private Integer torque;

	private BigDecimal mileage;

	@Column(name = "city_mileage")
	private BigDecimal cityMileage;

	@Column(name = "highway_mileage")
	private BigDecimal highwayMileage;

	@Column(name = "boot_space")
	private Integer bootSpace;

	@Column(name = "ground_clearance")
	private Integer groundClearance;

	@Column(length = 40)
	private String drivetrain;

	@Column(name = "ex_showroom_price", nullable = false, precision = 14, scale = 2)
	private BigDecimal exShowroomPrice;

	@Column(name = "on_road_price", precision = 14, scale = 2)
	private BigDecimal onRoadPrice;

	@Column(name = "image_url", length = 1024)
	private String imageUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "availability_status", nullable = false, length = 40)
	private AvailabilityStatus availabilityStatus;

	@Column(name = "search_count", nullable = false)
	@Builder.Default
	private Long searchCount = 0L;

	@Column(name = "view_count", nullable = false)
	@Builder.Default
	private Long viewCount = 0L;

	@OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<SafetyRating> safetyRatings = new ArrayList<>();

	@OneToOne(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
	private CarFeature features;

	@OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<UserReview> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CarImage> images = new ArrayList<>();
}
