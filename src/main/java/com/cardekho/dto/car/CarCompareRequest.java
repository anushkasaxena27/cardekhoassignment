package com.cardekho.dto.car;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CarCompareRequest(@NotEmpty @Size(min = 2, max = 4) List<Long> variantIds) {
}
