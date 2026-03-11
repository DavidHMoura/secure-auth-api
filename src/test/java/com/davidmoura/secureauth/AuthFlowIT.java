package com.davidmoura.secureauth;

import com.davidmoura.secureauth.config.TestSecurityConfig;
import com.davidmoura.secureauth.domain.User;
import com.davidmoura.secureauth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AuthFlowIT extends AbstractIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserRepository userRepository;

    @Test
    void register_verify_login_and_access_protected_endpoints() throws Exception {
        String email = "user_" + UUID.randomUUID() + "@example.com";
        String password = "StrongPassw0rd!";

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Test User",
                                "email", email,
                                "password", password
                        ))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andDo(print())
                .andExpect(status().isForbidden());

        User savedUser = userRepository.findByEmail(email).orElseThrow();
        String verificationToken = savedUser.getVerificationToken();

        mvc.perform(get("/api/v1/auth/verify-email")
                        .param("token", verificationToken))
                .andDo(print())
                .andExpect(status().isOk());

        var loginRes = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = om.readTree(loginRes);
        String accessToken = loginJson.get("accessToken").asText();
        String refreshToken = loginJson.get("refreshToken").asText();

        mvc.perform(get("/api/v1/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/admin/ping")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isForbidden());

        var refreshRes = mvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", refreshToken))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", refreshToken))))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        String newRefreshToken = om.readTree(refreshRes).get("refreshToken").asText();
        mvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", newRefreshToken))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private String json(Object value) throws Exception {
        return om.writeValueAsString(value);
    }
}