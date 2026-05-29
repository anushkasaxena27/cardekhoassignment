package com.cardekho.dto.car;

import java.math.BigDecimal;

public record CarFeatureDto(Boolean sunroof, Boolean ventilatedSeats, Boolean connectedCarTech,
		BigDecimal infotainmentSize, Boolean wirelessCharging, Boolean cruiseControl, Boolean panoramicSunroof,
		String audioSystem) {
}
