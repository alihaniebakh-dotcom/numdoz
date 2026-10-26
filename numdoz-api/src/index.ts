/// <reference types="@cloudflare/workers-types" />

export interface Env {
  DB: D1Database;
  BAZAAR_MOCK?: string;
  BAZAAR_ACCESS_TOKEN?: string;
}

const PRODUCTS: Record<string, { coins: number; title: string }> = {
  'coins_100':  { coins: 100,  title: '100 Coins' },
  'coins_500':  { coins: 500,  title: '500 Coins' },
  'coins_2000': { coins: 2000, title: '2000 Coins' },
};

const corsHeaders: Record<string, string> = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
  'Access-Control-Allow-Headers': 'Content-Type, Authorization',
};

function json(data: unknown, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: { ...corsHeaders, 'Content-Type': 'application/json' },
  });
}

function base64url(bytes: Uint8Array): string {
  let s = '';
  for (const b of bytes) s += String.fromCharCode(b);
  return btoa(s).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

async function getUserFromToken(env: Env, request: Request): Promise<{ id: string } | null> {
  const auth = request.headers.get('Authorization') || '';
  if (!auth.startsWith('Bearer ')) return null;
  const secret = auth.slice('Bearer '.length).trim();
  if (!secret || secret.length < 16) return null;
  const row = await env.DB.prepare('SELECT id FROM users WHERE session_secret = ?').bind(secret).first();
  if (!row) return null;
  return { id: (row as any).id };
}

async function getWallet(env: Env, userId: string) {
  return env.DB.prepare('SELECT * FROM wallet WHERE user_id = ?').bind(userId).first();
}

function isBazaarMock(env: Env): boolean {
  return env.BAZAAR_MOCK === 'true' || !env.BAZAAR_ACCESS_TOKEN;
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);
    const path = url.pathname;

    if (request.method === 'OPTIONS') {
      return new Response(null, { headers: corsHeaders });
    }

    try {
      if (path === '/' && request.method === 'GET') {
        return json({ status: 'ok', service: 'numdoz-api', version: '1.2.0' });
      }

      if (path === '/api/register' && request.method === 'POST') {
        const userId = crypto.randomUUID();
        const secretBytes = new Uint8Array(32);
        crypto.getRandomValues(secretBytes);
        const secret = base64url(secretBytes);
        const now = Date.now();
        await env.DB.batch([
          env.DB.prepare(
            `INSERT INTO users (id, username, avatar_id, created_at, updated_at, session_secret)
             VALUES (?, '', 0, ?, ?, ?)`
          ).bind(userId, now, now, secret),
          env.DB.prepare(
            `INSERT INTO wallet (user_id, coins, hints, capsules, updated_at)
             VALUES (?, 0, 3, 3, ?)`
          ).bind(userId, now),
        ]);
        return json({
          id: userId,
          sessionSecret: secret,
          wallet: { coins: 0, hints: 3, capsules: 3 },
        });
      }

      if (path === '/api/user' && request.method === 'POST') {
        const me = await getUserFromToken(env, request);
        if (!me) return json({ error: 'unauthorized' }, 401);
        const body: any = await request.json().catch(() => ({}));
        const now = Date.now();
        await env.DB.prepare(
          `UPDATE users SET username = COALESCE(?, username), avatar_id = COALESCE(?, avatar_id), updated_at = ? WHERE id = ?`
        ).bind(body.username ?? null, body.avatarId ?? null, now, me.id).run();
        return json({ ok: true });
      }

      if (path === '/api/me' && request.method === 'GET') {
        const me = await getUserFromToken(env, request);
        if (!me) return json({ error: 'unauthorized' }, 401);
        const [userRow, progressRows, walletRow] = await Promise.all([
          env.DB.prepare(`SELECT id, username, avatar_id, created_at, updated_at FROM users WHERE id = ?`).bind(me.id).all(),
          env.DB.prepare(`SELECT * FROM progress WHERE user_id = ?`).bind(me.id).all(),
          env.DB.prepare(`SELECT * FROM wallet WHERE user_id = ?`).bind(me.id).all(),
        ]);
        return json({
          user: (userRow.results && userRow.results[0]) || null,
          progress: progressRows.results || [],
          wallet: (walletRow.results && walletRow.results[0]) || null,
        });
      }

      if (path === '/api/sync' && request.method === 'POST') {
        const me = await getUserFromToken(env, request);
        if (!me) return json({ error: 'unauthorized' }, 401);
        const userId = me.id;
        const body: any = await request.json().catch(() => ({}));
        const now = Date.now();

        if (body.wallet) {
          await env.DB.prepare(
            `INSERT INTO wallet (user_id, coins, hints, capsules, updated_at)
             VALUES (?, ?, ?, ?, ?)
             ON CONFLICT(user_id) DO UPDATE SET
               coins = excluded.coins,
               hints = excluded.hints,
               capsules = excluded.capsules,
               updated_at = excluded.updated_at`
          ).bind(
            userId,
            body.wallet.coins ?? 0,
            body.wallet.hints ?? 0,
            body.wallet.capsules ?? 0,
            now
          ).run();
        }

        if (body.username !== undefined || body.avatarId !== undefined) {
          await env.DB.prepare(
            `UPDATE users SET username = COALESCE(?, username), avatar_id = COALESCE(?, avatar_id), updated_at = ? WHERE id = ?`
          ).bind(body.username ?? null, body.avatarId ?? null, now, userId).run();
        }

        if (Array.isArray(body.progress) && body.progress.length > 0) {
          const stmt = env.DB.prepare(
            `INSERT INTO progress (user_id, level_id, completed, stars, best_time, updated_at)
             VALUES (?, ?, ?, ?, ?, ?)
             ON CONFLICT(user_id, level_id) DO UPDATE SET
               completed = MAX(completed, excluded.completed),
               stars     = MAX(stars, excluded.stars),
               best_time = CASE
                 WHEN best_time = 0 OR (excluded.best_time > 0 AND excluded.best_time < best_time)
                 THEN excluded.best_time ELSE best_time
               END,
               updated_at = excluded.updated_at`
          );
          await env.DB.batch(
            body.progress.map((p: any) =>
              stmt.bind(userId, p.levelId, p.completed ? 1 : 0, p.stars ?? 0, p.bestTime ?? 0, now)
            )
          );
        }

        const [userRow, progressRows, walletRow] = await Promise.all([
          env.DB.prepare(`SELECT id, username, avatar_id, created_at, updated_at FROM users WHERE id = ?`).bind(userId).all(),
          env.DB.prepare(`SELECT * FROM progress WHERE user_id = ?`).bind(userId).all(),
          env.DB.prepare(`SELECT * FROM wallet WHERE user_id = ?`).bind(userId).all(),
        ]);

        return json({
          user: (userRow.results && userRow.results[0]) || null,
          progress: progressRows.results || [],
          wallet: (walletRow.results && walletRow.results[0]) || null,
        });
      }

      // --- NEW: product catalog ---
      if (path === '/api/products' && request.method === 'GET') {
        const products = Object.entries(PRODUCTS).map(([id, p]) => ({
          id, coins: p.coins, title: p.title,
        }));
        return json({ products, mock: isBazaarMock(env) });
      }

      // --- NEW: purchase verify ---
      if (path === '/api/purchase/verify' && request.method === 'POST') {
        const me = await getUserFromToken(env, request);
        if (!me) return json({ error: 'unauthorized' }, 401);

        const body: any = await request.json().catch(() => ({}));
        const productId: string | undefined = body.productId;
        const purchaseToken: string | undefined = body.purchaseToken;
        const orderId: string | undefined = body.orderId;

        if (!productId || !purchaseToken) {
          return json({ error: 'productId and purchaseToken required' }, 400);
        }
        const product = PRODUCTS[productId];
        if (!product) {
          return json({ error: 'unknown_product', productId }, 400);
        }

        const existing: any = await env.DB.prepare(
          'SELECT * FROM purchases WHERE purchase_token = ?'
        ).bind(purchaseToken).first();

        if (existing) {
          if (existing.user_id !== me.id) {
            return json({ error: 'token_belongs_to_other_user' }, 409);
          }
          const wallet = await getWallet(env, me.id);
          return json({
            ok: true,
            alreadyOwned: true,
            coinsGranted: existing.coins_granted,
            wallet,
          });
        }

        if (!isBazaarMock(env)) {
          // TODO: real Bazaar verify with BAZAAR_ACCESS_TOKEN
          return json({ error: 'bazaar_verify_not_implemented' }, 501);
        }

        const now = Date.now();
        const purchaseId = crypto.randomUUID();
        try {
          await env.DB.batch([
            env.DB.prepare(
              `INSERT INTO purchases
                 (id, user_id, product_id, purchase_token, order_id, coins_granted, status, verified_at, created_at)
               VALUES (?, ?, ?, ?, ?, ?, 'verified', ?, ?)`
            ).bind(purchaseId, me.id, productId, purchaseToken, orderId || null, product.coins, now, now),
            env.DB.prepare(
              `INSERT INTO wallet (user_id, coins, hints, capsules, updated_at)
               VALUES (?, ?, 3, 3, ?)
               ON CONFLICT(user_id) DO UPDATE SET
                 coins = coins + excluded.coins,
                 updated_at = excluded.updated_at`
            ).bind(me.id, product.coins, now),
          ]);
        } catch (e: any) {
          if (String(e.message || e).includes('UNIQUE')) {
            const wallet = await getWallet(env, me.id);
            return json({ ok: true, alreadyOwned: true, wallet });
          }
          throw e;
        }

        const wallet = await getWallet(env, me.id);
        return json({ ok: true, coinsGranted: product.coins, wallet });
      }

      return json({ error: 'not found', path }, 404);
    } catch (e: any) {
      return json({ error: e.message || String(e) }, 500);
    }
  },
};