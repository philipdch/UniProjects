ALTER TABLE users 
ADD COLUMN failed_attempts int,
ADD COLUMN locked boolean,
ADD COLUMN lock_time timestamp,
ADD COLUMN enabled boolean,
ADD COLUMN pwd_last_modified timestamp;
