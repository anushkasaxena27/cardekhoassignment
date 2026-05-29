package com.cardekho.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "make_id", nullable = false)
	private CarMake make;

	@Column(name = "model_name", nullable = false, length = 200)
	private String modelName;

	@Column(length = 80)
	private String segment;

	@Column(name = "body_type", length = 80)
	private String bodyType;

	@Column(name = "launch_year")
	private Integer launchYear;

	@OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CarVariant> variants = new ArrayList<>();
}
