package com.cardekho.service;

import com.cardekho.entity.SearchLog;
import com.cardekho.repository.CarVariantRepository;
import com.cardekho.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchLogService {

	private final SearchLogRepository searchLogRepository;
	private final CarVariantRepository carVariantRepository;

	/** Separate read-write transaction so callers can stay {@code readOnly = true} (e.g. recommendation chat). */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logQuery(String query, Long variantId) {
		SearchLog.SearchLogBuilder b = SearchLog.builder().queryText(query);
		if (variantId != null) {
			b.variant(carVariantRepository.getReferenceById(variantId));
		}
		searchLogRepository.save(b.build());
	}
}
