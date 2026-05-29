package com.cardekho.security;

import com.cardekho.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

	private final JwtProperties props;

	public JwtService(JwtProperties props) {
		this.props = props;
	}

	public String createAccessToken(String subject, Map<String, Object> claims) {
		Instant now = Instant.now();
		Instant exp = now.plusSeconds(props.getAccessTokenMinutes() * 60);
		var builder = Jwts.builder()
				.subject(subject)
				.issuedAt(Date.from(now))
				.expiration(Date.from(exp));
		for (var e : claims.entrySet()) {
			builder.claim(e.getKey(), e.getValue());
		}
		return builder.signWith(signingKey()).compact();
	}

	public Claims parse(String token) {
		return Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey signingKey() {
		byte[] keyBytes = props.getSecret().getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			byte[] padded = new byte[32];
			System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
			keyBytes = padded;
		}
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
