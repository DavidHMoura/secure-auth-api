CREATE TABLE IF NOT EXISTS audit_events (
                                            id          UUID        PRIMARY KEY,
                                            event_type  VARCHAR(50) NOT NULL,
    user_id     UUID,
    ip_address  VARCHAR(45),
    detail      VARCHAR(255),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_audit_user_id   ON audit_events(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_event_type ON audit_events(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON audit_events(created_at);