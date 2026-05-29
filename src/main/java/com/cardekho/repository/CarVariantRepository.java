package com.cardekho.repository;

import com.cardekho.entity.CarVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarVariantRepository extends JpaRepository<CarVariant, Long>, JpaSpecificationExecutor<CarVariant> {

	@Query("""
			select distinct v from CarVariant v
			join fetch v.model m
			join fetch m.make
			left join fetch v.features
			where v.id = :id
			""")
	Optional<CarVariant> findDetailById(@Param("id") Long id);

	@Modifying
	@Query("update CarVariant v set v.viewCount = v.viewCount + 1 where v.id = :id")
	void incrementViewCount(@Param("id") Long id);

	@Modifying
	@Query("update CarVariant v set v.searchCount = v.searchCount + 1 where v.id = :id")
	void incrementSearchCount(@Param("id") Long id);

	@Query("""
			select v from CarVariant v
			join fetch v.model m
			join fetch m.make
			order by v.searchCount desc
			""")
	List<CarVariant> findTopTrending(Pageable pageable);

	List<CarVariant> findTop10ByOrderBySearchCountDesc();

	List<CarVariant> findTop10ByOrderByViewCountDesc();
}
