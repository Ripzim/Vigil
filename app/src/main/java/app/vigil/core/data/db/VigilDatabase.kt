package app.vigil.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(
    entities = [
        BlockedDomainEntity::class,
        AllowedDomainEntity::class,
        ProtectionEventEntity::class,
        RecoveryLogEntity::class,
        TriggerEntity::class,
        BibleReadingEntity::class,
        FavoriteEntity::class,
        WatchSessionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class VigilDatabase : RoomDatabase() {
    abstract fun blockedDomainDao(): BlockedDomainDao
    abstract fun allowedDomainDao(): AllowedDomainDao
    abstract fun protectionEventDao(): ProtectionEventDao
    abstract fun recoveryLogDao(): RecoveryLogDao
    abstract fun triggerDao(): TriggerDao
    abstract fun bibleReadingDao(): BibleReadingDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun watchSessionDao(): WatchSessionDao
}

/**
 * Migrações do banco. A versão 1 é o esquema inicial, então a lista está vazia.
 * Toda mudança de esquema deve incrementar `version` e adicionar uma [Migration] aqui,
 * sem usar migração destrutiva (os registros do usuário são privados e insubstituíveis).
 */
object Migrations {
    val ALL: Array<Migration> = emptyArray()
}
