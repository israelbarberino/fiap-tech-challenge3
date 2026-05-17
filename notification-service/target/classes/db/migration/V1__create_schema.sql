CREATE SCHEMA IF NOT EXISTS schema_notification;

CREATE TABLE IF NOT EXISTS schema_notification.notification_log (
    id UUID PRIMARY KEY,
    event_fingerprint VARCHAR(128) NOT NULL UNIQUE,
    event_type VARCHAR(80) NOT NULL,
    appointment_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    payload_json TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_attempt_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_notification_status ON schema_notification.notification_log (status);
