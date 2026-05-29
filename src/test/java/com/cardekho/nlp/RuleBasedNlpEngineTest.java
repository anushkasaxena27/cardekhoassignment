package com.cardekho.nlp;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RuleBasedNlpEngineTest {

	private final RuleBasedNlpEngine engine = new RuleBasedNlpEngine();

	@Test
	void extractsBudgetAndSuv() {
		ExtractedIntent intent = engine.extract("I need a safe SUV under 20 lakhs with good mileage");
		assertThat(intent.getBudgetMaxInr()).isEqualByComparingTo(BigDecimal.valueOf(2_000_000));
		assertThat(intent.getBodyTypes()).contains("SUV");
		assertThat(intent.getFeatureSignals()).contains("safety", "mileage");
	}

	@Test
	void extractsBetweenBudget() {
		ExtractedIntent intent = engine.extract("between 10 and 15 lakhs automatic diesel");
		assertThat(intent.getBudgetMinInr()).isEqualByComparingTo(BigDecimal.valueOf(1_000_000));
		assertThat(intent.getBudgetMaxInr()).isEqualByComparingTo(BigDecimal.valueOf(1_500_000));
		assertThat(intent.getTransmissionPreference()).isEqualTo("Automatic");
		assertThat(intent.getFuelTypes()).contains("Diesel");
	}
}
