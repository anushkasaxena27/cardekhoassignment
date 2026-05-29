package com.cardekho.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TextSearchRequest(
		@NotBlank @Size(max = 500) String q) {
}
