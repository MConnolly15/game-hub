ALTER TABLE users
RENAME COLUMN name TO username;

ALTER TABLE users
ALTER COLUMN username TYPE VARCHAR(16);

ALTER TABLE users
ADD CONSTRAINT users_username_unique UNIQUE (username);