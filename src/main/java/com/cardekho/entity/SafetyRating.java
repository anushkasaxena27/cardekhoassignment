package com.cardekho.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "safety_rating")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafetyRating {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "variant_id", nullable = false)
	private CarVariant variant;

	@Column(nullable = false, length = 80)
	private String agency;

	private BigDecimal rating;

	private Integer airbags;

	@Column(nullable = false)
	@Builder.Default
	private Boolean abs = false;

	@Column(nullable = false)
	@Builder.Default
	private Boolean esc = false;

	@Column(name = "adas_features", length = 500)
	private String adasFeatures;
}
