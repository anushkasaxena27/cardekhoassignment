package com.cardekho.util;

import java.math.BigDecimal;

/** Hibernate/JPQL aggregates often return {@link Double} in {@code Object[]} rows even for DECIMAL columns. */
public final class JpaScalars {

	private JpaScalars() {
	}

	public static BigDecimal bigDecimal(Object value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		if (value instanceof BigDecimal bd) {
			return bd;
		}
		if (value instanceof Number n) {
			return BigDecimal.valueOf(n.doubleValue());
		}
		return new BigDecimal(value.toString());
	}
}
