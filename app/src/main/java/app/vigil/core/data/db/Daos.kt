package app.vigil.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedDomainDao {
    @Query("SELECT * FROM blocked_domains ORDER BY domain")
    fun observeAll(): Flow<List<BlockedDomainEntity>>

    /** Retorna -1 quando o domínio já existe. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: BlockedDomainEntity): Long

    @Query("DELETE FROM blocked_domains WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface AllowedDomainDao {
    @Query("SELECT * FROM allowed_domains ORDER BY domain")
    fun observeAll(): Flow<List<AllowedDomainEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: AllowedDomainEntity): Long

    @Query("DELETE FROM allowed_domains WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ProtectionEventDao {
    @Query("SELECT * FROM protection_events ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<ProtectionEventEntity>>

    @Insert
    suspend fun insert(entity: ProtectionEventEntity): Long
}

@Dao
interface RecoveryLogDao {
    @Query("SELECT * FROM recovery_logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<RecoveryLogEntity>>

    @Insert
    suspend fun insert(entity: RecoveryLogEntity): Long

    @Query("DELETE FROM recovery_logs WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface TriggerDao {
    @Query("SELECT * FROM triggers ORDER BY name")
    fun observeAll(): Flow<List<TriggerEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: TriggerEntity): Long

    @Query("DELETE FROM triggers WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface BibleReadingDao {
    @Query("SELECT * FROM bible_readings ORDER BY readAt DESC")
    fun observeAll(): Flow<List<BibleReadingEntity>>

    @Insert
    suspend fun insert(entity: BibleReadingEntity): Long
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE readingKey = :key")
    suspend fun delete(key: String)
}

@Dao
interface WatchSessionDao {
    @Query("SELECT * FROM watch_sessions ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<WatchSessionEntity>>

    @Insert
    suspend fun insert(entity: WatchSessionEntity): Long
}
