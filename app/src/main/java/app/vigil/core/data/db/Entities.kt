package app.vigil.core.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Domínio bloqueado pelo VIGIL Shield (filtro DNS chega na Fase 2). */
@Entity(tableName = "blocked_domains", indices = [Index(value = ["domain"], unique = true)])
data class BlockedDomainEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val domain: String,
    val createdAt: Long,
)

/** Domínio sempre permitido. Na Fase 2 a lista de permissões terá precedência sobre o bloqueio. */
@Entity(tableName = "allowed_domains", indices = [Index(value = ["domain"], unique = true)])
data class AllowedDomainEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val domain: String,
    val createdAt: Long,
)

/** Evento de proteção. Guarda apenas tipo e detalhe opcional — nunca URLs visitadas. */
@Entity(tableName = "protection_events")
data class ProtectionEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val type: String,
    val detail: String? = null,
)

/** Registro de vontade ou recaída (ver [RecoveryKind]). */
@Entity(tableName = "recovery_logs")
data class RecoveryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val kind: String,
    val triggerName: String? = null,
    val intensity: Int,
    val note: String? = null,
)

/** Catálogo pessoal de gatilhos. */
@Entity(tableName = "triggers", indices = [Index(value = ["name"], unique = true)])
data class TriggerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long,
)

/** Histórico de leituras devocionais (readingKey referencia [app.vigil.feature.devotion.DevotionContent]). */
@Entity(tableName = "bible_readings")
data class BibleReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val readingKey: String,
    val readAt: Long,
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val readingKey: String,
    val createdAt: Long,
)

/** Sessão de pausa consciente do VIGIL Watch. */
@Entity(tableName = "watch_sessions")
data class WatchSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,
    val durationSec: Int,
    val completed: Boolean,
    val activity: String? = null,
)

enum class RecoveryKind { URGE, LAPSE }
