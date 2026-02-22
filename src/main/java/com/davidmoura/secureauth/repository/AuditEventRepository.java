package com.davidmoura.secureauth.repository;

import com.davidmoura.secureauth.domain.AuditEvent;
import com.davidmoura.secureauth.domain.AuditEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    List<AuditEvent> findByUserId(UUID userId);

    List<AuditEvent> findByEventType(AuditEventType eventType);
}