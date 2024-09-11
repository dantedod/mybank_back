ALTER TABLE users
ALTER COLUMN id TYPE bigint USING id::bigint;