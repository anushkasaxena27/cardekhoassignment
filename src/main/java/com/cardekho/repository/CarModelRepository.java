package com.cardekho.repository;

import com.cardekho.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {
	List<CarModel> findByMakeId(Long makeId);
}
