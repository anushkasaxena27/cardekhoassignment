package com.cardekho.nlp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RuleBasedNlpEngine {

	private static final BigDecimal LAKH = BigDecimal.valueOf(100_000);
	private static final BigDecimal CRORE = BigDecimal.valueOf(10_000_000);

	private static final Pattern BETWEEN_LAKH = Pattern.compile(
			"(?:between|from)\\s+(\\d+(?:\\.\\d+)?)\\s*(?:and|-|to)\\s+(\\d+(?:\\.\\d+)?)\\s*(lakh|lakhs|lac)\\b",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern UNDER_LAKH = Pattern.compile(
			"(?:under|below|upto|up to|less than|max(?:imum)?)\\s+(\\d+(?:\\.\\d+)?)\\s*(lakh|lakhs|lac)\\b",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern UNDER_CRORE = Pattern.compile(
			"(?:under|below|less than)\\s+(\\d+(?:\\.\\d+)?)\\s*(crore|crores)\\b", Pattern.CASE_INSENSITIVE);

	public ExtractedIntent extract(String rawQuery) {
		if (rawQuery == null || rawQuery.isBlank()) {
			return ExtractedIntent.builder().build();
		}
		String q = rawQuery.toLowerCase(Locale.ROOT);
		ExtractedIntent.ExtractedIntentBuilder b = ExtractedIntent.builder();
		applyBudget(q, b);

		if (q.contains("suv")) {
			b.bodyType("SUV");
		}
		if (q.contains("sedan")) {
			b.bodyType("Sedan");
		}
		if (q.contains("hatchback")) {
			b.bodyType("Hatchback");
		}
		if (q.contains("muv") || q.contains("mpv")) {
			b.bodyType("MUV");
		}
		if (containsAny(q, "ev", "electric", "battery")) {
			b.bodyType("EV");
			b.fuelType("Electric");
		}
		if (q.contains("diesel")) {
			b.fuelType("Diesel");
		}
		if (q.contains("petrol")) {
			b.fuelType("Petrol");
		}
		if (q.contains("hybrid")) {
			b.fuelType("Hybrid");
		}
		if (q.contains("cng")) {
			b.fuelType("CNG");
		}
		if (containsAny(q, "automatic", "auto ", " amt", "dct", "cvt", "torque converter")) {
			b.transmissionPreference("Automatic");
		} else if (q.contains("manual")) {
			b.transmissionPreference("Manual");
		}
		if (containsAny(q, "city", "traffic", "commute")) {
			b.usageIntent("city");
		}
		if (containsAny(q, "highway", "touring", "long drive")) {
			b.usageIntent("highway");
		}
		if (containsAny(q, "family", "kids", "seven seater", "7 seater", "6 seater")) {
			b.usageIntent("family");
		}
		if (containsAny(q, "off-road", "offroad", "4x4", "awd")) {
			b.usageIntent("offroad");
		}
		if (containsAny(q, "sunroof", "panoramic")) {
			b.featureSignal("sunroof");
		}
		if (containsAny(q, "mileage", "fuel economy", "efficient", "economy")) {
			b.featureSignal("mileage");
		}
		if (containsAny(q, "safe", "safety", "airbag", "ncap", "adas")) {
			b.featureSignal("safety");
		}
		if (containsAny(q, "comfort", "cushy", "ride quality")) {
			b.featureSignal("comfort");
		}
		if (containsAny(q, "luxury", "premium", "plush")) {
			b.featureSignal("luxury");
		}
		if (containsAny(q, "sporty", "fun to drive", "enthusiast", "performance")) {
			b.featureSignal("sporty");
		}
		if (containsAny(q, "low maintenance", "reliable", "warranty")) {
			b.sentimentSignal("low_maintenance");
		}
		if (containsAny(q, "good mileage", "great mileage")) {
			b.sentimentSignal("good_mileage");
		}
		if (containsAny(q, "premium feel", "upmarket")) {
			b.sentimentSignal("premium_feel");
		}
		return b.build();
	}

	private static boolean containsAny(String q, String... needles) {
		for (String n : needles) {
			if (q.contains(n)) {
				return true;
			}
		}
		return false;
	}

	private static void applyBudget(String q, ExtractedIntent.ExtractedIntentBuilder b) {
		Matcher mBetween = BETWEEN_LAKH.matcher(q);
		if (mBetween.find()) {
			BigDecimal min = lakh(new BigDecimal(mBetween.group(1)));
			BigDecimal max = lakh(new BigDecimal(mBetween.group(2)));
			b.budgetMinInr(min.min(max));
			b.budgetMaxInr(max.max(min));
			return;
		}
		Matcher mUnder = UNDER_LAKH.matcher(q);
		if (mUnder.find()) {
			b.budgetMaxInr(lakh(new BigDecimal(mUnder.group(1))));
			return;
		}
		Matcher mCrore = UNDER_CRORE.matcher(q);
		if (mCrore.find()) {
			b.budgetMaxInr(crore(new BigDecimal(mCrore.group(1))));
		}
	}

	private static BigDecimal lakh(BigDecimal n) {
		return n.multiply(LAKH).setScale(0, RoundingMode.HALF_UP);
	}

	private static BigDecimal crore(BigDecimal n) {
		return n.multiply(CRORE).setScale(0, RoundingMode.HALF_UP);
	}
}
