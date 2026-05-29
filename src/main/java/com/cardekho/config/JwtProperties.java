package com.cardekho.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
	private String secret;
	private long accessTokenMinutes = 15;
	private long refreshTokenDays = 14;
}
