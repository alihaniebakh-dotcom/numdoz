package ir.numdoz.data.remote

import ir.numdoz.data.local.DataStore
import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.UserProfile
import ir.numdoz.util.currentTimeMillis

/**
 * Applies cloud data to the local store on boot.
 *
 * Safety-first rule: only overwrite local when local is "fresh".
 * Existing local data always wins — we push it up on the next change.
 */
class CloudMerge(private val localStore: DataStore) {

    suspend fun applyIfLocalEmpty(me: MeResponse) {
        try {
            applyProfile(me)
            applyWallet(me)
            applyProgress(me)
        } catch (t: Throwable) {
            println("[NumDoz/cloud] merge failed: ${t.message}")
        }
    }

    private suspend fun applyProfile(me: MeResponse) {
        val cloudUser = me.user ?: return
        if (cloudUser.username.isBlank() && cloudUser.avatarId == 0) return

        val local = localStore.getProfile()
        if (local.exists) {
            println("[NumDoz/cloud] merge: local profile exists ('${local.username}'), keeping local")
            return
        }

        val now = currentTimeMillis()
        localStore.updateProfile(
            UserProfile(
                username = cloudUser.username,
                avatarId = cloudUser.avatarId,
                createdAt = if (cloudUser.createdAt > 0) cloudUser.createdAt else now,
                lastLoginAt = now,
                totalSessions = 1
            )
        )
        println("[NumDoz/cloud] merge: applied cloud profile ('${cloudUser.username}', avatar=${cloudUser.avatarId})")
    }

    private suspend fun applyWallet(me: MeResponse) {
        val cloudWallet = me.wallet ?: return

        val local = localStore.getWallet()
        val localIsPristine = local.coins == 0 && local.totalAdsWatched == 0
        if (!localIsPristine) {
            println("[NumDoz/cloud] merge: local wallet is active (coins=${local.coins}), keeping local")
            return
        }

        if (cloudWallet.coins == 0 && cloudWallet.hints == local.freeHints) {
            println("[NumDoz/cloud] merge: cloud wallet is default, nothing to apply")
            return
        }

        localStore.updateWallet(
            local.copy(
                coins = cloudWallet.coins,
                freeHints = cloudWallet.hints,
                freeTimeExtensions = cloudWallet.capsules
            )
        )
        println("[NumDoz/cloud] merge: applied cloud wallet (coins=${cloudWallet.coins}, hints=${cloudWallet.hints}, ext=${cloudWallet.capsules})")
    }

    private suspend fun applyProgress(me: MeResponse) {
        if (me.progress.isEmpty()) return

        val localRows = localStore.getAllLevelProgress()
        if (localRows.isNotEmpty()) {
            println("[NumDoz/cloud] merge: local has ${localRows.size} progress rows, keeping local")
            return
        }

        for (p in me.progress) {
            localStore.upsertLevelProgress(
                LevelProgress(
                    levelId = p.levelId,
                    completed = p.completed > 0,
                    stars = p.stars,
                    bestTimeSeconds = p.bestTime
                )
            )
        }
        println("[NumDoz/cloud] merge: applied ${me.progress.size} progress rows from cloud")
    }
}