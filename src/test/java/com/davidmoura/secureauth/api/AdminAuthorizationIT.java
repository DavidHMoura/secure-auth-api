package com.davidmoura.secureauth.api;

import com.davidmoura.secureauth.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AdminAuthorizationIT {

    private final MockMvc mvc;

    AdminAuthorizationIT(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_endpoint_allows_admin() throws Exception {
        mvc.perform(get("/api/v1/admin/ping"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void admin_endpoint_denies_user() throws Exception {
        mvc.perform(get("/api/v1/admin/ping"))
                .andExpect(status().isForbidden());
    }
}
