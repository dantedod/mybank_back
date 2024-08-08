-- V1__alter_users_id_to_bigint.sql
ALTER TABLE users
ALTER COLUMN id TYPE bigint USING id::bigint;

