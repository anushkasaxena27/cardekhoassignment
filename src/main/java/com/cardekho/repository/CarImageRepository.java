package com.cardekho.repository;

import com.cardekho.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {
	List<CarImage> findByVariantIdOrderByIdAsc(Long variantId);
}
