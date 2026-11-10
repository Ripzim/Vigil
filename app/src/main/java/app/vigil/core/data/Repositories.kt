package app.vigil.core.data

import app.vigil.core.data.db.AllowedDomainDao
import app.vigil.core.data.db.AllowedDomainEntity
import app.vigil.core.data.db.BibleReadingDao
import app.vigil.core.data.db.BibleReadingEntity
import app.vigil.core.data.db.BlockedDomainDao
import app.vigil.core.data.db.BlockedDomainEntity
import app.vigil.core.data.db.FavoriteDao
import app.vigil.core.data.db.FavoriteEntity
import app.vigil.core.data.db.ProtectionEventDao
import app.vigil.core.data.db.RecoveryKind
import app.vigil.core.data.db.RecoveryLogDao
import app.vigil.core.data.db.RecoveryLogEntity
import app.vigil.core.data.db.TriggerDao
import app.vigil.core.data.db.TriggerEntity
import app.vigil.core.data.db.WatchSessionDao
import app.vigil.core.data.db.WatchSessionEntity
import app.vigil.core.util.DomainNormalizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class DomainResult { ADDED, INVALID, DUPLICATE }

class ShieldRepository(
    private val blockedDao: BlockedDomainDao,
    private val allowedDao: AllowedDomainDao,
    eventDao: ProtectionEventDao,
) {
    val blocked: Flow<List<BlockedDomainEntity>> = blockedDao.observeAll()
    val allowed: Flow<List<AllowedDomainEntity>> = allowedDao.observeAll()
    val events = eventDao.observeRecent(50)

    suspend fun addBlocked(raw: String): DomainResult {
        val domain = DomainNormalizer.normalize(raw) ?: return DomainResult.INVALID
        val id = blockedDao.insert(BlockedDomainEntity(domain = domain, createdAt = System.currentTimeMillis()))
        return if (id == -1L) DomainResult.DUPLICATE else DomainResult.ADDED
    }

    suspend fun addAllowed(raw: String): DomainResult {
        val domain = DomainNormalizer.normalize(raw) ?: return DomainResult.INVALID
        val id = allowedDao.insert(AllowedDomainEntity(domain = domain, createdAt = System.currentTimeMillis()))
        return if (id == -1L) DomainResult.DUPLICATE else DomainResult.ADDED
    }

    suspend fun removeBlocked(id: Long) = blockedDao.delete(id)
    suspend fun removeAllowed(id: Long) = allowedDao.delete(id)
}

class RecoveryRepository(
    private val logDao: RecoveryLogDao,
    private val triggerDao: TriggerDao,
) {
    val logs: Flow<List<RecoveryLogEntity>> = logDao.observeAll()
    val triggers: Flow<List<TriggerEntity>> = triggerDao.observeAll()

    suspend fun add(kind: RecoveryKind, triggerName: String?, intensity: Int, note: String?) {
        logDao.insert(
            RecoveryLogEntity(
                timestamp = System.currentTimeMillis(),
                kind = kind.name,
                triggerName = triggerName?.takeIf { it.isNotBlank() },
                intensity = intensity.coerceIn(1, 5),
                note = note?.trim()?.takeIf { it.isNotEmpty() },
            ),
        )
    }

    suspend fun deleteLog(id: Long) = logDao.delete(id)

    suspend fun addTrigger(name: String): Boolean {
        val clean = name.trim()
        if (clean.isEmpty()) return false
        return triggerDao.insert(TriggerEntity(name = clean, createdAt = System.currentTimeMillis())) != -1L
    }
}

class WatchRepository(private val dao: WatchSessionDao) {
    val sessions: Flow<List<WatchSessionEntity>> = dao.observeAll()

    suspend fun record(startedAt: Long, durationSec: Int, completed: Boolean, activity: String?) {
        dao.insert(
            WatchSessionEntity(
                startedAt = startedAt,
                durationSec = durationSec,
                completed = completed,
                activity = activity,
            ),
        )
    }
}

class DevotionRepository(
    private val readingDao: BibleReadingDao,
    private val favoriteDao: FavoriteDao,
) {
    val readings: Flow<List<BibleReadingEntity>> = readingDao.observeAll()
    val favoriteKeys: Flow<Set<String>> = favoriteDao.observeAll().map { list -> list.map { it.readingKey }.toSet() }

    suspend fun markRead(key: String) {
        readingDao.insert(BibleReadingEntity(readingKey = key, readAt = System.currentTimeMillis()))
    }

    suspend fun setFavorite(key: String, favorite: Boolean) {
        if (favorite) {
            favoriteDao.insert(FavoriteEntity(readingKey = key, createdAt = System.currentTimeMillis()))
        } else {
            favoriteDao.delete(key)
        }
    }
}
