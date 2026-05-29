package com.cardekho.repository;

import com.cardekho.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByTokenHash(String tokenHash);

	@Modifying
	@Query("delete from RefreshToken r where r.expiresAt < :now")
	void deleteExpired(@Param("now") Instant now);

	@Modifying
	void deleteAllByUser_Id(Long userId);
}
