package com.davidmoura.secureauth.service;

import com.davidmoura.secureauth.domain.AuditEvent;
import com.davidmoura.secureauth.domain.AuditEventType;
import com.davidmoura.secureauth.repository.AuditEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditEventRepository repository;

    public AuditService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener
    @Transactional
    public void onAuditEvent(AuthAuditEvent event) {
        try {
            repository.save(new AuditEvent(
                    event.type(),
                    event.userId(),
                    event.ipAddress(),
                    event.detail()
            ));
        } catch (Exception e) {
            log.error("Failed to persist audit event [{}]: {}", event.type(), e.getMessage());
        }
    }

    public record AuthAuditEvent(
            AuditEventType type,
            UUID userId,
            String ipAddress,
            String detail
    ) {}
}