INSERT INTO schema_appointment.app_user (id, username, password_hash, role, active)
VALUES
    (gen_random_uuid(), 'doctor1', crypt('doctor123', gen_salt('bf')), 'ROLE_DOCTOR', true),
    (gen_random_uuid(), 'nurse1', crypt('nurse123', gen_salt('bf')), 'ROLE_NURSE', true),
    (gen_random_uuid(), 'patient1', crypt('patient123', gen_salt('bf')), 'ROLE_PATIENT', true)
ON CONFLICT (username) DO NOTHING;
