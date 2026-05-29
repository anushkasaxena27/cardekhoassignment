package com.cardekho.repository;

import com.cardekho.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

	@Query("""
			select s.variant.id, count(s) from SearchLog s
			where s.variant is not null
			group by s.variant.id
			order by count(s) desc
			""")
	List<Object[]> countByVariantPopular(org.springframework.data.domain.Pageable pageable);
}
