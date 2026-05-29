package com.cardekho.repository;

import com.cardekho.entity.SafetyRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SafetyRatingRepository extends JpaRepository<SafetyRating, Long> {
	List<SafetyRating> findByVariantId(Long variantId);

	@Query("""
			select s from SafetyRating s
			join fetch s.variant v
			join fetch v.model m
			join fetch m.make
			where s.rating is not null
			order by s.rating desc
			""")
	List<SafetyRating> findTopRated(Pageable pageable);
}
