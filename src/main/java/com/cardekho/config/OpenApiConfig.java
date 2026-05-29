package com.cardekho.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI carDekhoOpenApi() {
		final String scheme = "bearerAuth";
		return new OpenAPI()
				.info(new Info().title("CarDekho Conversational API").version("v1")
						.description("REST APIs for conversational car recommendations, catalog, and analytics."))
				.addSecurityItem(new SecurityRequirement().addList(scheme))
				.components(new Components().addSecuritySchemes(scheme,
						new SecurityScheme()
								.name(scheme)
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")));
	}
}
