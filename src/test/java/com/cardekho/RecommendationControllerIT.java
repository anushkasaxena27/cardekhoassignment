package com.cardekho;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecommendationControllerIT {

	@Autowired
	MockMvc mockMvc;

	@Test
	void chatReturnsRecommendations() throws Exception {
		mockMvc.perform(post("/api/recommendations/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"query\":\"safe SUV under 20 lakhs with good mileage\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.recommendations").isArray())
				.andExpect(jsonPath("$.recommendations[0].car").exists())
				.andExpect(jsonPath("$.extractedIntent.budgetMaxInr").value(2000000));
	}
}
