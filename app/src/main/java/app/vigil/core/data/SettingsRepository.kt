package app.vigil.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.vigilDataStore: DataStore<Preferences> by preferencesDataStore(name = "vigil_settings")

/** Configurações do app, armazenadas localmente no DataStore. */
class SettingsRepository(private val store: DataStore<Preferences>) {
    private val advancedKey = booleanPreferencesKey("advanced_dashboard")

    val advancedDashboard: Flow<Boolean> = store.data.map { it[advancedKey] ?: false }

    suspend fun setAdvancedDashboard(value: Boolean) {
        store.edit { it[advancedKey] = value }
    }

    suspend fun clear() {
        store.edit { it.clear() }
    }
}
