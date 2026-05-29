package com.cardekho.dto.common;

import java.time.Instant;
import java.util.List;

public record ApiError(String message, int status, Instant timestamp, List<String> details) {
}
