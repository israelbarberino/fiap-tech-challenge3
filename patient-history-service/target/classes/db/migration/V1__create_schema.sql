CREATE SCHEMA IF NOT EXISTS schema_history;

CREATE TABLE IF NOT EXISTS schema_history.consultation_history (
    id UUID PRIMARY KEY,
    appointment_id UUID NOT NULL UNIQUE,
    patient_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL,
    notes TEXT,
    status VARCHAR(30) NOT NULL,
    source_event_type VARCHAR(80) NOT NULL,
    last_synced_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_history_patient ON schema_history.consultation_history (patient_id);
CREATE INDEX IF NOT EXISTS idx_history_doctor ON schema_history.consultation_history (doctor_id);
CREATE INDEX IF NOT EXISTS idx_history_scheduled_at ON schema_history.consultation_history (scheduled_at);
