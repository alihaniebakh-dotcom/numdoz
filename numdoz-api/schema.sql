CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    username TEXT NOT NULL DEFAULT '',
    avatar_id INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL DEFAULT 0,
    updated_at INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS progress (
    user_id TEXT NOT NULL,
    level_id TEXT NOT NULL,
    completed INTEGER NOT NULL DEFAULT 0,
    stars INTEGER NOT NULL DEFAULT 0,
    best_time INTEGER NOT NULL DEFAULT 0,
    updated_at INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, level_id)
);

CREATE TABLE IF NOT EXISTS wallet (
    user_id TEXT PRIMARY KEY,
    coins INTEGER NOT NULL DEFAULT 0,
    hints INTEGER NOT NULL DEFAULT 3,
    capsules INTEGER NOT NULL DEFAULT 3,
    updated_at INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_progress_user ON progress(user_id);
