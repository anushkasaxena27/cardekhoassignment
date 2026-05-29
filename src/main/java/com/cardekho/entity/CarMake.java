package com.cardekho.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_make")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarMake {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 120)
	private String name;

	@Column(length = 120)
	private String country;

	@Column(name = "logo_url", length = 1024)
	private String logoUrl;

	@OneToMany(mappedBy = "make", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CarModel> models = new ArrayList<>();
}
