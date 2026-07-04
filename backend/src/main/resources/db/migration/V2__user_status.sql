-- Replace the simple is_deleted flag with an explicit lifecycle: status + enabled/locked flags.
-- New users start PENDING and disabled until an admin approves and assigns a role.

ALTER TABLE users DROP COLUMN is_deleted;

ALTER TABLE users ADD COLUMN is_enabled  BOOLEAN     NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN is_locked   BOOLEAN     NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN user_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
