package com.cardekho.controller;

import com.cardekho.dto.auth.LoginRequest;
import com.cardekho.dto.auth.RefreshRequest;
import com.cardekho.dto.auth.RegisterRequest;
import com.cardekho.dto.auth.TokenResponse;
import com.cardekho.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register a new user")
	public void register(@Valid @RequestBody RegisterRequest request) {
		authService.register(request);
	}

	@PostMapping("/login")
	@Operation(summary = "Login and obtain JWT tokens")
	public TokenResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/refresh")
	@Operation(summary = "Refresh access token using refresh token")
	public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
		return authService.refresh(request);
	}
}
