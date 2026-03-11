package com.davidmoura.secureauth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SecureAuth IAM API",
                description = "Advanced Identity and Access Management API. Features include stateless JWT authentication, anti-enumeration defenses, rate limiting, and secure password reset flows.",
                version = "v1.0.0",
                contact = @Contact(
                        name = "David Moura",
                        email = "david@ciberseguranca.com"
                        // url = "COLOQUE_SEU_LINKEDIN_AQUI"
                )
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Input your JWT Access Token here to access secured endpoints.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}