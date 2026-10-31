package ir.numdoz.data.local

import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.UserProfile
import ir.numdoz.data.model.UserWallet
import ir.numdoz.data.remote.CloudSync

/**
 * Decorator around DataStore that pushes changes to the cloud.
 * All other methods delegate untouched.
 */
class SyncingDataStore(
    private val delegate: DataStore,
    private val cloudSync: CloudSync
) : DataStore by delegate {

    override suspend fun updateWallet(wallet: UserWallet) {
        delegate.updateWallet(wallet)
        cloudSync.pushWalletAsync(wallet)
    }

    override suspend fun upsertLevelProgress(progress: LevelProgress) {
        delegate.upsertLevelProgress(progress)
        cloudSync.pushProgressAsync(progress)
    }

    override suspend fun updateProfile(profile: UserProfile) {
        delegate.updateProfile(profile)
        cloudSync.pushProfileAsync(profile)
    }
}