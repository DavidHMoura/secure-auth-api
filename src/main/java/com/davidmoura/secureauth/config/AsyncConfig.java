package com.davidmoura.secureauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Habilita:
 * - @Async: para o AuditService registrar eventos sem bloquear a thread da request.
 * - @EnableScheduling: para futuros jobs de limpeza (ex: purge de refresh tokens expirados).
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {}