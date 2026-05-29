package com.cardekho.repository.spec;

import com.cardekho.entity.CarMake;
import com.cardekho.entity.CarModel;
import com.cardekho.entity.CarVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class CarVariantSpecifications {

	private CarVariantSpecifications() {
	}

	public static Specification<CarVariant> withFilters(
			Long makeId,
			String bodyType,
			String fuelType,
			String transmission,
			BigDecimal minPrice,
			BigDecimal maxPrice,
			String segment) {
		return (root, query, cb) -> {
			if (query != null) {
				query.distinct(true);
			}
			List<Predicate> p = new ArrayList<>();
			Join<CarVariant, CarModel> model = root.join("model", JoinType.INNER);
			Join<CarModel, CarMake> make = model.join("make", JoinType.INNER);

			if (makeId != null) {
				p.add(cb.equal(make.get("id"), makeId));
			}
			if (bodyType != null && !bodyType.isBlank()) {
				p.add(cb.equal(cb.lower(model.get("bodyType")), bodyType.toLowerCase()));
			}
			if (fuelType != null && !fuelType.isBlank()) {
				p.add(cb.equal(cb.lower(root.get("fuelType")), fuelType.toLowerCase()));
			}
			if (transmission != null && !transmission.isBlank()) {
				p.add(cb.equal(cb.lower(root.get("transmission")), transmission.toLowerCase()));
			}
			if (minPrice != null) {
				p.add(cb.greaterThanOrEqualTo(root.get("exShowroomPrice"), minPrice));
			}
			if (maxPrice != null) {
				p.add(cb.lessThanOrEqualTo(root.get("exShowroomPrice"), maxPrice));
			}
			if (segment != null && !segment.isBlank()) {
				p.add(cb.equal(cb.lower(model.get("segment")), segment.toLowerCase()));
			}
			if (p.isEmpty()) {
				return cb.conjunction();
			}
			return cb.and(p.toArray(Predicate[]::new));
		};
	}

	public static Specification<CarVariant> searchText(String q) {
		if (q == null || q.isBlank()) {
			return (root, query, cb) -> cb.conjunction();
		}
		String token = "%" + q.toLowerCase() + "%";
		return (root, query, cb) -> {
			if (query != null) {
				query.distinct(true);
			}
			Join<CarVariant, CarModel> model = root.join("model", JoinType.INNER);
			Join<CarModel, CarMake> make = model.join("make", JoinType.INNER);
			jakarta.persistence.criteria.Expression<String> full = cb.concat(make.get("name"), " ");
			full = cb.concat(full, model.get("modelName"));
			full = cb.concat(full, " ");
			full = cb.concat(full, root.get("variantName"));
			return cb.like(cb.lower(full), token);
		};
	}
}
