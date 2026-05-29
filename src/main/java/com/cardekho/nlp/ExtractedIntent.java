package com.cardekho.nlp;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class ExtractedIntent {
	private BigDecimal budgetMinInr;
	private BigDecimal budgetMaxInr;
	@Singular("bodyType")
	private Set<String> bodyTypes;
	@Singular("fuelType")
	private Set<String> fuelTypes;
	@Singular("usageIntent")
	private Set<String> usageIntents;
	@Singular("featureSignal")
	private Set<String> featureSignals;
	@Singular("sentimentSignal")
	private Set<String> sentimentSignals;
	private String transmissionPreference;
}
