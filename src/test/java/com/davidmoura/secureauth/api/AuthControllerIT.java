package com.davidmoura.secureauth.api;

import com.davidmoura.secureauth.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AuthControllerIT {

    private final MockMvc mvc;

    AuthControllerIT(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void login_returns_401_when_credentials_invalid() throws Exception {
        String body = """
                { "email": "nope@nope.com", "password": "wrong" }
                """;

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
