package com.cardekho.repository;

import com.cardekho.entity.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
	List<UserReview> findByVariantId(Long variantId);

	@Query("""
			select coalesce(avg(r.rating), 0) from UserReview r where r.variant.id = :variantId
			""")
	BigDecimal averageRatingForVariant(@Param("variantId") Long variantId);

	@Query("""
			select count(r) from UserReview r where r.variant.id = :variantId
			""")
	long countByVariantId(@Param("variantId") Long variantId);

	@Query("""
			select r.variant.id, avg(r.rating) from UserReview r
			where r.variant.id in :ids group by r.variant.id
			""")
	List<Object[]> averageRatingsByVariantIds(@Param("ids") Collection<Long> ids);
}
