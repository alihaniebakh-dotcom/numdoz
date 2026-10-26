ALTER TABLE users ADD COLUMN session_secret TEXT;
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_secret
  ON users(session_secret) WHERE session_secret IS NOT NULL;

DELETE FROM users    WHERE id = 'test-user-001';
DELETE FROM wallet   WHERE user_id = 'test-user-001';
DELETE FROM progress WHERE user_id = 'test-user-001';