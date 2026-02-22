package com.davidmoura.secureauth.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_audit_user_id",    columnList = "userId"),
        @Index(name = "idx_audit_event_type", columnList = "eventType"),
        @Index(name = "idx_audit_created_at", columnList = "createdAt")
})
public class AuditEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditEventType eventType;

    @Column
    private UUID userId;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 255)
    private String detail;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected AuditEvent() {}

    public AuditEvent(AuditEventType eventType, UUID userId, String ipAddress, String detail) {
        this.eventType = eventType;
        this.userId    = userId;
        this.ipAddress = ipAddress;
        this.detail    = detail;
    }

    public UUID getId()                { return id; }
    public AuditEventType getEventType() { return eventType; }
    public UUID getUserId()            { return userId; }
    public String getIpAddress()       { return ipAddress; }
    public String getDetail()          { return detail; }
    public Instant getCreatedAt()      { return createdAt; }
}