package com.cardekho.service;

import com.cardekho.dto.car.CarFilterRequest;
import com.cardekho.dto.recommendation.*;
import com.cardekho.entity.CarVariant;
import com.cardekho.nlp.ExtractedIntent;
import com.cardekho.nlp.RuleBasedNlpEngine;
import com.cardekho.recommendation.RecommendationScorer;
import com.cardekho.repository.CarVariantRepository;
import com.cardekho.repository.UserReviewRepository;
import com.cardekho.repository.UserShortlistRepository;
import com.cardekho.repository.spec.CarVariantSpecifications;
import com.cardekho.util.JpaScalars;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

	private final RuleBasedNlpEngine nlpEngine;
	private final CarVariantRepository carVariantRepository;
	private final RecommendationScorer recommendationScorer;
	private final UserReviewRepository userReviewRepository;
	private final UserShortlistRepository userShortlistRepository;
	private final SearchLogService searchLogService;

	@Transactional(readOnly = true)
	public ChatRecommendationResponse chat(ChatRecommendationRequest request) {
		searchLogService.logQuery(request.query(), null);
		ExtractedIntent intent = nlpEngine.extract(request.query());
		CarFilterRequest filters = IntentFilterMapper.toCarFilter(intent);
		Specification<CarVariant> spec = CarVariantSpecifications.withFilters(
				filters.makeId(),
				filters.bodyType(),
				filters.fuelType(),
				filters.transmission(),
				filters.minPrice(),
				filters.maxPrice(),
				filters.segment());

		List<CarVariant> candidates = carVariantRepository.findAll(spec,
				PageRequest.of(0, 160, Sort.by(Sort.Direction.ASC, "exShowroomPrice"))).getContent();

		if (candidates.isEmpty()) {
			candidates = carVariantRepository.findAll(PageRequest.of(0, 160)).getContent();
		}

		Set<Long> preferredMakes = resolvePreferredMakeIds();

		List<Long> ids = candidates.stream().map(CarVariant::getId).toList();
		Map<Long, BigDecimal> reviewAvg = ids.isEmpty() ? Map.of()
				: userReviewRepository.averageRatingsByVariantIds(ids).stream()
						.collect(Collectors.toMap(r -> (Long) r[0], r -> JpaScalars.bigDecimal(r[1])));

		record Scored(CarVariant v, int score, List<String> reasons) {
		}

		List<Scored> scored = candidates.stream()
				.map(v -> {
					var breakdown = recommendationScorer.score(v, intent, preferredMakes, reviewAvg);
					return new Scored(v, breakdown.points(), breakdown.reasons());
				})
				.sorted(Comparator.comparingInt(Scored::score).reversed())
				.limit(10)
				.toList();

		List<RecommendationItemDto> recs = scored.stream()
				.map(s -> new RecommendationItemDto(
						s.v().getId(),
						s.v().getModel().getMake().getName() + " " + s.v().getModel().getModelName() + " " + s.v().getVariantName(),
						s.score(),
						s.reasons(),
						s.v().getImageUrl(),
						s.v().getExShowroomPrice(),
						s.v().getMileage()))
				.toList();

		ExtractedIntentDto dto = new ExtractedIntentDto(
				intent.getBudgetMinInr(),
				intent.getBudgetMaxInr(),
				Set.copyOf(intent.getBodyTypes()),
				Set.copyOf(intent.getFuelTypes()),
				Set.copyOf(intent.getUsageIntents()),
				Set.copyOf(intent.getFeatureSignals()),
				Set.copyOf(intent.getSentimentSignals()),
				intent.getTransmissionPreference());

		return new ChatRecommendationResponse(dto, filters, recs);
	}

	private Set<Long> resolvePreferredMakeIds() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			return Set.of();
		}
		String email = auth.getName();
		return userShortlistRepository.findByUser_EmailIgnoreCaseOrderByCreatedAtDesc(email).stream()
				.map(us -> us.getVariant().getModel().getMake().getId())
				.collect(Collectors.toSet());
	}

	static final class IntentFilterMapper {
		private IntentFilterMapper() {
		}

		static CarFilterRequest toCarFilter(ExtractedIntent intent) {
			String body = intent.getBodyTypes().stream()
					.filter(b -> !"EV".equalsIgnoreCase(b))
					.findFirst()
					.orElse(null);
			String fuel = intent.getBodyTypes().contains("EV") ? "Electric"
					: intent.getFuelTypes().stream().findFirst().orElse(null);
			String segment = intent.getFeatureSignals().contains("luxury") ? "Luxury" : null;
			return new CarFilterRequest(
					null,
					body,
					fuel,
					intent.getTransmissionPreference(),
					intent.getBudgetMinInr(),
					intent.getBudgetMaxInr(),
					segment);
		}
	}
}
