UPDATE users
SET failed_attempts = 0;

UPDATE users
SET locked = false;

UPDATE users
SET enabled = true;

UPDATE users
SET pwd_last_modified = current_timestamp;