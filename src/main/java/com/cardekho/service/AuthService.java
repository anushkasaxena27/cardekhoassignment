package com.cardekho.service;

import com.cardekho.config.JwtProperties;
import com.cardekho.dto.auth.LoginRequest;
import com.cardekho.dto.auth.RefreshRequest;
import com.cardekho.dto.auth.RegisterRequest;
import com.cardekho.dto.auth.TokenResponse;
import com.cardekho.entity.AppUser;
import com.cardekho.entity.RefreshToken;
import com.cardekho.exception.BadRequestException;
import com.cardekho.repository.AppUserRepository;
import com.cardekho.repository.RefreshTokenRepository;
import com.cardekho.security.JwtService;
import com.cardekho.security.TokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AppUserRepository users;
	private final RefreshTokenRepository refreshTokens;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;
	private final TokenHasher tokenHasher;

	@Transactional
	public void register(RegisterRequest request) {
		if (users.existsByEmailIgnoreCase(request.email())) {
			throw new BadRequestException("Email already registered");
		}
		AppUser user = AppUser.builder()
				.email(request.email().trim().toLowerCase())
				.passwordHash(passwordEncoder.encode(request.password()))
				.fullName(request.fullName())
				.role("USER")
				.build();
		users.save(user);
	}

	@Transactional
	public TokenResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		AppUser user = users.findByEmailIgnoreCase(request.email())
				.orElseThrow(() -> new BadRequestException("User not found"));
		return issueTokens(user);
	}

	@Transactional
	public TokenResponse refresh(RefreshRequest request) {
		String hash = tokenHasher.sha256Hex(request.refreshToken());
		RefreshToken stored = refreshTokens.findByTokenHash(hash)
				.orElseThrow(() -> new BadRequestException("Invalid refresh token"));
		if (stored.getExpiresAt().isBefore(Instant.now())) {
			throw new BadRequestException("Refresh token expired");
		}
		AppUser user = users.findById(stored.getUser().getId()).orElseThrow();
		refreshTokens.delete(stored);
		return issueTokens(user);
	}

	private TokenResponse issueTokens(AppUser user) {
		String access = jwtService.createAccessToken(user.getEmail(),
				Map.of("role", user.getRole(), "uid", String.valueOf(user.getId())));
		String refresh = UUID.randomUUID().toString();
		Instant exp = Instant.now().plus(jwtProperties.getRefreshTokenDays(), ChronoUnit.DAYS);
		refreshTokens.save(RefreshToken.builder()
				.user(user)
				.tokenHash(tokenHasher.sha256Hex(refresh))
				.expiresAt(exp)
				.createdAt(Instant.now())
				.build());
		refreshTokens.deleteExpired(Instant.now());
		long ttl = jwtProperties.getAccessTokenMinutes() * 60;
		return new TokenResponse(access, refresh, ttl);
	}
}
