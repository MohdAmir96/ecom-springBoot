package com.amir.ecommerce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:ecommerce-test",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"app.jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
		"app.jwt.expiration-ms=3600000"
})
@AutoConfigureMockMvc
class EcommerceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void registrationReturnsTokenAndTokenAuthenticatesProtectedRoutes() throws Exception {
		String username = "test-" + UUID.randomUUID();
		String requestBody = """
				{"userName":"%s","password":"test-password-123"}
				""".formatted(username);

		MvcResult registration = mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andReturn();

		String token = com.jayway.jsonpath.JsonPath.read(
				registration.getResponse().getContentAsString(), "$.accessToken");

		mockMvc.perform(get("/").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void protectedRoutesRejectRequestsWithoutToken() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isUnauthorized());
	}

}
