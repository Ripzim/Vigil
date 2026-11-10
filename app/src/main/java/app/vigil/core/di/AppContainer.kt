package app.vigil.core.di

import android.content.Context
import androidx.room.Room
import app.vigil.core.data.DevotionRepository
import app.vigil.core.data.RecoveryRepository
import app.vigil.core.data.SettingsRepository
import app.vigil.core.data.ShieldRepository
import app.vigil.core.data.WatchRepository
import app.vigil.core.data.db.Migrations
import app.vigil.core.data.db.VigilDatabase
import app.vigil.core.data.vigilDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Injeção de dependências manual. Um único contêiner por processo, criado em [app.vigil.VigilApp]. */
class AppContainer(context: Context) {
    private val database: VigilDatabase = Room.databaseBuilder(
        context.applicationContext,
        VigilDatabase::class.java,
        "vigil.db",
    ).addMigrations(*Migrations.ALL).build()

    val settings = SettingsRepository(context.applicationContext.vigilDataStore)
    val shield = ShieldRepository(database.blockedDomainDao(), database.allowedDomainDao(), database.protectionEventDao())
    val recovery = RecoveryRepository(database.recoveryLogDao(), database.triggerDao())
    val watch = WatchRepository(database.watchSessionDao())
    val devotion = DevotionRepository(database.bibleReadingDao(), database.favoriteDao())

    /** Apaga todos os dados do usuário: banco Room e configurações. */
    suspend fun wipeAllData() = withContext(Dispatchers.IO) {
        database.clearAllTables()
        settings.clear()
    }
}
