/// <reference types="@cloudflare/workers-types" />

export interface Env {
  DB: D1Database;
}

const corsHeaders: Record<string, string> = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
  'Access-Control-Allow-Headers': 'Content-Type',
};

function json(data: unknown, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: { ...corsHeaders, 'Content-Type': 'application/json' },
  });
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
        return json({ status: 'ok', service: 'numdoz-api', version: '1.0.0' });
      }

      if (path === '/api/user' && request.method === 'POST') {
        const body: any = await request.json();
        if (!body.id || typeof body.id !== 'string') {
          return json({ error: 'id required' }, 400);
        }
        const now = Date.now();
        await env.DB.prepare(
          `INSERT INTO users (id, username, avatar_id, created_at, updated_at)
           VALUES (?, ?, ?, ?, ?)
           ON CONFLICT(id) DO UPDATE SET
             username = excluded.username,
             avatar_id = excluded.avatar_id,
             updated_at = excluded.updated_at`
        ).bind(body.id, body.username || '', body.avatarId || 0, now, now).run();
        return json({ ok: true });
      }

      if (path.startsWith('/api/user/') && request.method === 'GET') {
        const id = decodeURIComponent(path.substring('/api/user/'.length));
        const { results } = await env.DB.prepare(
          `SELECT * FROM users WHERE id = ?`
        ).bind(id).all();
        if (!results || results.length === 0) {
          return json({ error: 'not found' }, 404);
        }
        return json(results[0]);
      }

      if (path === '/api/sync' && request.method === 'POST') {
        const body: any = await request.json();
        const userId: string | undefined = body.id;
        if (!userId) {
          return json({ error: 'id required' }, 400);
        }
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
            `INSERT INTO users (id, username, avatar_id, created_at, updated_at)
             VALUES (?, ?, ?, ?, ?)
             ON CONFLICT(id) DO UPDATE SET
               username = excluded.username,
               avatar_id = excluded.avatar_id,
               updated_at = excluded.updated_at`
          ).bind(userId, body.username || '', body.avatarId || 0, now, now).run();
        }

        if (Array.isArray(body.progress) && body.progress.length > 0) {
          const stmt = env.DB.prepare(
            `INSERT INTO progress (user_id, level_id, completed, stars, best_time, updated_at)
             VALUES (?, ?, ?, ?, ?, ?)
             ON CONFLICT(user_id, level_id) DO UPDATE SET
               completed = MAX(completed, excluded.completed),
               stars = MAX(stars, excluded.stars),
               best_time = CASE
                 WHEN best_time = 0 OR excluded.best_time < best_time
                 THEN excluded.best_time ELSE best_time
               END,
               updated_at = excluded.updated_at`
          );
          const batch = body.progress.map((p: any) =>
            stmt.bind(
              userId,
              p.levelId,
              p.completed ? 1 : 0,
              p.stars ?? 0,
              p.bestTime ?? 0,
              now
            )
          );
          await env.DB.batch(batch);
        }

        const [userRow, progressRows, walletRow] = await Promise.all([
          env.DB.prepare(`SELECT * FROM users WHERE id = ?`).bind(userId).all(),
          env.DB.prepare(`SELECT * FROM progress WHERE user_id = ?`).bind(userId).all(),
          env.DB.prepare(`SELECT * FROM wallet WHERE user_id = ?`).bind(userId).all(),
        ]);

        return json({
          user: (userRow.results && userRow.results[0]) || null,
          progress: progressRows.results || [],
          wallet: (walletRow.results && walletRow.results[0]) || null,
        });
      }

      return json({ error: 'not found', path }, 404);
    } catch (e: any) {
      return json({ error: e.message || String(e) }, 500);
    }
  },
};
