package com.cardekho.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "variant_id", nullable = false)
	private CarVariant variant;

	@Column(nullable = false, length = 120)
	private String username;

	@Column(nullable = false, precision = 2, scale = 1)
	private BigDecimal rating;

	@Column(length = 255)
	private String title;

	@Column(name = "review_text", columnDefinition = "TEXT")
	private String reviewText;

	@Column(columnDefinition = "TEXT")
	private String pros;

	@Column(columnDefinition = "TEXT")
	private String cons;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
