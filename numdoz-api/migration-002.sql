CREATE TABLE IF NOT EXISTS purchases (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    product_id TEXT NOT NULL,
    purchase_token TEXT NOT NULL,
    order_id TEXT,
    coins_granted INTEGER NOT NULL DEFAULT 0,
    status TEXT NOT NULL DEFAULT 'verified',
    verified_at INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_purchases_token ON purchases(purchase_token);
CREATE INDEX IF NOT EXISTS idx_purchases_user ON purchases(user_id);