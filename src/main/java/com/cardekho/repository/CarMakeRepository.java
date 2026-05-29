package com.cardekho.repository;

import com.cardekho.entity.CarMake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarMakeRepository extends JpaRepository<CarMake, Long> {
	Optional<CarMake> findByNameIgnoreCase(String name);
}
