package com.davidmoura.secureauth;

import com.davidmoura.secureauth.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = SecureAuthApiApplication.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class SecureAuthApiApplicationTests {

	@Test
	void contextLoads() {
	}
}
