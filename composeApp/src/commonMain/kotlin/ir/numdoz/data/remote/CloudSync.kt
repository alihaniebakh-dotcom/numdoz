package ir.numdoz.data.remote

import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.UserProfile
import ir.numdoz.data.model.UserWallet
import ir.numdoz.data.local.DataStore
import ir.numdoz.data.session.SessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Fire-and-forget cloud pushes.
 * Runs in a scope tied to App composition; lost pushes are harmless because
 * the next change re-pushes and server-side mutations are idempotent (MAX / CASE).
 */
class CloudSync(
    private val apiClient: ApiClient,
    private val sessionStore: SessionStore,
    private val scope: CoroutineScope
) {
    fun pushWalletAsync(wallet: UserWallet) {
        scope.launch {
            runCatching {
                val secret = sessionStore.getSecret() ?: return@runCatching
                apiClient.sync(
                    secret,
                    SyncRequest(
                        wallet = WalletRequest(
                            coins = wallet.coins,
                            hints = wallet.freeHints,
                            capsules = wallet.freeTimeExtensions
                        )
                    )
                )
                println("[NumDoz/cloud] pushWallet ok: coins=${wallet.coins}, hints=${wallet.freeHints}")
            }.onFailure {
                println("[NumDoz/cloud] pushWallet failed: ${it.message}")
            }
        }
    }

    fun pushProgressAsync(progress: LevelProgress) {
        scope.launch {
            runCatching {
                val secret = sessionStore.getSecret() ?: return@runCatching
                apiClient.sync(
                    secret,
                    SyncRequest(
                        progress = listOf(
                            ProgressRequest(
                                levelId = progress.levelId,
                                completed = progress.completed,
                                stars = progress.stars,
                                bestTime = progress.bestTimeSeconds
                            )
                        )
                    )
                )
                println("[NumDoz/cloud] pushProgress ok: ${progress.levelId} stars=${progress.stars} best=${progress.bestTimeSeconds}")
            }.onFailure {
                println("[NumDoz/cloud] pushProgress failed (${progress.levelId}): ${it.message}")
            }
        }
    }

    fun pushProfileAsync(profile: UserProfile) {
        scope.launch {
            runCatching {
                val secret = sessionStore.getSecret() ?: return@runCatching
                apiClient.sync(
                    secret,
                    SyncRequest(
                        username = profile.username.takeIf { it.isNotBlank() },
                        avatarId = profile.avatarId
                    )
                )
                println("[NumDoz/cloud] pushProfile ok: username='${profile.username}', avatar=${profile.avatarId}")
            }.onFailure {
                println("[NumDoz/cloud] pushProfile failed: ${it.message}")
            }
        }
    }

    /**
     * Pushes ALL local data to the cloud. Called only once, right after
     * a brand-new registration — so the server has a complete snapshot
     * instead of only the deltas that happen after this point.
     */
    fun fullPushIfFirstSession(localStore: DataStore) {
        scope.launch {
            runCatching {
                val secret = sessionStore.getSecret() ?: return@runCatching

                val profile = localStore.getProfile()
                if (profile.exists) {
                    pushProfileAsync(profile)
                }

                val wallet = localStore.getWallet()
                pushWalletAsync(wallet)

                val progress = localStore.getAllLevelProgress()
                if (progress.isNotEmpty()) {
                    apiClient.sync(
                        secret,
                        SyncRequest(
                            progress = progress.map { p ->
                                ProgressRequest(
                                    levelId = p.levelId,
                                    completed = p.completed,
                                    stars = p.stars,
                                    bestTime = p.bestTimeSeconds
                                )
                            }
                        )
                    )
                    println("[NumDoz/cloud] fullPush ok: ${progress.size} progress rows")
                }
            }.onFailure {
                println("[NumDoz/cloud] fullPush failed: ${it.message}")
            }
        }
    }
}
