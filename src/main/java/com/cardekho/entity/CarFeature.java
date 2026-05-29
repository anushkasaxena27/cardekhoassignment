package com.cardekho.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "car_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarFeature {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "variant_id", nullable = false, unique = true)
	private CarVariant variant;

	@Column(nullable = false)
	@Builder.Default
	private Boolean sunroof = false;

	@Column(name = "ventilated_seats", nullable = false)
	@Builder.Default
	private Boolean ventilatedSeats = false;

	@Column(name = "connected_car_tech", nullable = false)
	@Builder.Default
	private Boolean connectedCarTech = false;

	@Column(name = "infotainment_size", precision = 4, scale = 1)
	private BigDecimal infotainmentSize;

	@Column(name = "wireless_charging", nullable = false)
	@Builder.Default
	private Boolean wirelessCharging = false;

	@Column(name = "cruise_control", nullable = false)
	@Builder.Default
	private Boolean cruiseControl = false;

	@Column(name = "panoramic_sunroof", nullable = false)
	@Builder.Default
	private Boolean panoramicSunroof = false;

	@Column(name = "audio_system", length = 200)
	private String audioSystem;
}
