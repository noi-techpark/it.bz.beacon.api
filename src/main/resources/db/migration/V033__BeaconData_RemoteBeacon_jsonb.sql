ALTER TABLE beacon_data ADD COLUMN user_updated_at timestamp without time zone;
UPDATE beacon_data SET user_updated_at = created_at;

ALTER TABLE beacon_data ADD COLUMN remote_beacon jsonb;
ALTER TABLE beacon_data ADD COLUMN remote_beacon_updated_at timestamp without time zone;
