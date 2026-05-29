package com.cardekho.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "car_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "variant_id", nullable = false)
	private CarVariant variant;

	@Column(name = "image_url", nullable = false, length = 1024)
	private String imageUrl;

	@Column(name = "image_type", length = 40)
	private String imageType;
}
