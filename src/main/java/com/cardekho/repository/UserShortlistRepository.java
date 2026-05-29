package com.cardekho.repository;

import com.cardekho.entity.UserShortlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserShortlistRepository extends JpaRepository<UserShortlist, Long> {
	List<UserShortlist> findByUser_EmailIgnoreCaseOrderByCreatedAtDesc(String email);

	@Query("""
			select us from UserShortlist us
			join fetch us.variant v
			join fetch v.model m
			join fetch m.make
			left join fetch v.features
			where us.user.id = :userId
			order by us.createdAt desc
			""")
	List<UserShortlist> findDetailedByUserId(@Param("userId") Long userId);

	Optional<UserShortlist> findByUser_IdAndVariant_Id(Long userId, Long variantId);

	void deleteByUser_IdAndVariant_Id(Long userId, Long variantId);
}
