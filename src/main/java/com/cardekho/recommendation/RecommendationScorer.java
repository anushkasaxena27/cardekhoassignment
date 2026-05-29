package com.cardekho.recommendation;

import com.cardekho.entity.CarFeature;
import com.cardekho.entity.CarVariant;
import com.cardekho.entity.SafetyRating;
import com.cardekho.nlp.ExtractedIntent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class RecommendationScorer {

	public record ScoreBreakdown(int points, List<String> reasons) {
	}

	public ScoreBreakdown score(CarVariant v, ExtractedIntent intent, Set<Long> preferredMakeIds,
			java.util.Map<Long, BigDecimal> reviewAverages) {
		List<String> reasons = new ArrayList<>();
		double weighted = 0;
		double wsum = 0;

		double budget = scoreBudget(v, intent);
		weighted += budget * 0.25;
		wsum += 0.25;
		if (budget > 75) {
			reasons.add("Fits your budget");
		}

		double safety = scoreSafety(v, intent);
		weighted += safety * 0.20;
		wsum += 0.20;
		if (safety > 80 && intent.getFeatureSignals().contains("safety")) {
			reasons.add("Strong safety credentials");
		}

		double mileage = scoreMileage(v, intent);
		weighted += mileage * 0.15;
		wsum += 0.15;
		if (mileage > 80 && (intent.getFeatureSignals().contains("mileage") || intent.getSentimentSignals().contains("good_mileage"))) {
			reasons.add("Great mileage for everyday use");
		}

		double reviews = scoreReviews(v, reviewAverages);
		weighted += reviews * 0.15;
		wsum += 0.15;
		if (reviews > 80) {
			reasons.add("Highly rated by owners");
		}

		double features = scoreFeatures(v, intent);
		weighted += features * 0.12;
		wsum += 0.12;
		if (features > 75 && intent.getFeatureSignals().contains("sunroof")) {
			reasons.add("Sunroof / premium cabin features");
		}

		double usage = scoreUsage(v, intent);
		weighted += usage * 0.08;
		wsum += 0.08;
		if (usage > 75 && intent.getUsageIntents().contains("city")) {
			reasons.add("Comfortable for city driving");
		}

		double popularity = scorePopularity(v);
		weighted += popularity * 0.03;
		wsum += 0.03;

		double value = scorePriceToFeatureValue(v);
		weighted += value * 0.02;
		wsum += 0.02;
		if (value > 80) {
			reasons.add("Strong price-to-features value");
		}

		double base = wsum > 0 ? weighted / wsum : 0;
		int boost = preferredMakeIds.contains(v.getModel().getMake().getId()) ? 5 : 0;
		if (boost > 0) {
			reasons.add("Aligned with brands in your shortlist");
		}
		int points = (int) Math.round(Math.min(100, base + boost));
		if (reasons.isEmpty()) {
			reasons.add("Balanced match for your query");
		}
		return new ScoreBreakdown(points, reasons.stream().distinct().limit(6).toList());
	}

	private static double scoreBudget(CarVariant v, ExtractedIntent intent) {
		BigDecimal price = v.getExShowroomPrice();
		BigDecimal max = intent.getBudgetMaxInr();
		BigDecimal min = intent.getBudgetMinInr();
		if (max == null && min == null) {
			return 70;
		}
		if (max != null && price.compareTo(max.multiply(BigDecimal.valueOf(1.05))) <= 0) {
			return 100;
		}
		if (max != null && price.compareTo(max.multiply(BigDecimal.valueOf(1.15))) <= 0) {
			return 80;
		}
		if (min != null && max != null && price.compareTo(min) >= 0 && price.compareTo(max) <= 0) {
			return 100;
		}
		if (max != null) {
			BigDecimal over = price.subtract(max).max(BigDecimal.ZERO);
			BigDecimal ratio = over.divide(max.max(BigDecimal.ONE), 4, RoundingMode.HALF_UP);
			double penalty = Math.min(90, ratio.doubleValue() * 200);
			return Math.max(10, 100 - penalty);
		}
		return 60;
	}

	private static double scoreSafety(CarVariant v, ExtractedIntent intent) {
		Optional<BigDecimal> best = v.getSafetyRatings().stream()
				.map(SafetyRating::getRating)
				.filter(r -> r != null)
				.max(Comparator.naturalOrder());
		double base = best.map(r -> r.divide(BigDecimal.valueOf(5), 4, RoundingMode.HALF_UP).doubleValue() * 100)
				.orElse(55.0);
		if (intent.getFeatureSignals().contains("safety")) {
			return base;
		}
		return base * 0.85 + 10;
	}

	private static double scoreMileage(CarVariant v, ExtractedIntent intent) {
		BigDecimal m = v.getMileage();
		if (m == null) {
			return 50;
		}
		double kmpl = m.doubleValue();
		double score = Math.min(100, (kmpl / 22.0) * 100);
		if (intent.getUsageIntents().contains("highway") && v.getHighwayMileage() != null) {
			score = Math.max(score, Math.min(100, v.getHighwayMileage().doubleValue() / 24.0 * 100));
		}
		if (intent.getUsageIntents().contains("city") && v.getCityMileage() != null) {
			score = Math.max(score, Math.min(100, v.getCityMileage().doubleValue() / 18.0 * 100));
		}
		return score;
	}

	private static double scoreReviews(CarVariant v, java.util.Map<Long, BigDecimal> reviewAverages) {
		BigDecimal avg = reviewAverages.getOrDefault(v.getId(), BigDecimal.ZERO);
		if (avg == null || avg.compareTo(BigDecimal.ZERO) == 0) {
			return 60;
		}
		return avg.divide(BigDecimal.valueOf(5), 4, RoundingMode.HALF_UP).doubleValue() * 100;
	}

	private static double scoreFeatures(CarVariant v, ExtractedIntent intent) {
		CarFeature f = v.getFeatures();
		double s = 55;
		if (f == null) {
			return s;
		}
		if (Boolean.TRUE.equals(f.getPanoramicSunroof()) || Boolean.TRUE.equals(f.getSunroof())) {
			s += 10;
		}
		if (Boolean.TRUE.equals(f.getVentilatedSeats())) {
			s += 8;
		}
		if (Boolean.TRUE.equals(f.getWirelessCharging())) {
			s += 4;
		}
		if (Boolean.TRUE.equals(f.getConnectedCarTech())) {
			s += 5;
		}
		if (intent.getFeatureSignals().contains("luxury") && f.getAudioSystem() != null && f.getAudioSystem().toLowerCase().contains("bose")) {
			s += 8;
		}
		if (intent.getFeatureSignals().contains("sporty") && v.getHorsepower() != null && v.getHorsepower() >= 150) {
			s += 10;
		}
		return Math.min(100, s);
	}

	private static double scoreUsage(CarVariant v, ExtractedIntent intent) {
		double s = 60;
		if (intent.getUsageIntents().contains("family") && v.getSeatingCapacity() != null && v.getSeatingCapacity() >= 6) {
			s += 25;
		}
		if (intent.getUsageIntents().contains("offroad")) {
			if (v.getGroundClearance() != null && v.getGroundClearance() >= 200) {
				s += 15;
			}
			if (v.getDrivetrain() != null && v.getDrivetrain().toLowerCase().contains("awd")) {
				s += 20;
			}
		}
		if (intent.getUsageIntents().contains("city") && "Automatic".equalsIgnoreCase(v.getTransmission())) {
			s += 10;
		}
		return Math.min(100, s);
	}

	private static double scorePopularity(CarVariant v) {
		long c = v.getSearchCount() == null ? 0 : v.getSearchCount();
		return Math.min(100, Math.log10(c + 1) * 25);
	}

	private static double scorePriceToFeatureValue(CarVariant v) {
		CarFeature f = v.getFeatures();
		int features = 0;
		if (f != null) {
			if (Boolean.TRUE.equals(f.getSunroof())) {
				features++;
			}
			if (Boolean.TRUE.equals(f.getPanoramicSunroof())) {
				features++;
			}
			if (Boolean.TRUE.equals(f.getVentilatedSeats())) {
				features++;
			}
			if (Boolean.TRUE.equals(f.getWirelessCharging())) {
				features++;
			}
			if (Boolean.TRUE.equals(f.getConnectedCarTech())) {
				features++;
			}
			if (Boolean.TRUE.equals(f.getCruiseControl())) {
				features++;
			}
		}
		double priceLakh = v.getExShowroomPrice().divide(LAKH, 4, RoundingMode.HALF_UP).doubleValue();
		double density = features / Math.max(1.0, priceLakh);
		return Math.min(100, density * 40 + 40);
	}

	private static final BigDecimal LAKH = BigDecimal.valueOf(100_000);
}
