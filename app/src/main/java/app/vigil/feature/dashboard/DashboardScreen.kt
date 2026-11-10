package app.vigil.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import app.vigil.core.data.db.RecoveryKind
import app.vigil.core.di.AppContainer
import app.vigil.core.di.vigilViewModel
import app.vigil.core.ui.EmptyState
import app.vigil.core.ui.Hairline
import app.vigil.core.ui.KeyValueRow
import app.vigil.core.ui.SectionLabel
import app.vigil.core.ui.VigilScreen
import app.vigil.core.ui.theme.VigilColors
import app.vigil.core.util.DAY_MS
import app.vigil.feature.devotion.DevotionContent
import app.vigil.feature.devotion.DevotionReading
import app.vigil.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardState(
    val advanced: Boolean = false,
    val recoveryLogs7d: Int = 0,
    val watchCompleted7d: Int = 0,
    val readings7d: Int = 0,
    val nextReading: DevotionReading? = DevotionContent.readings.firstOrNull(),
    // Somente modo avançado
    val urges30d: Int = 0,
    val lapses30d: Int = 0,
    val blockedCount: Int = 0,
    val allowedCount: Int = 0,
    val favoritesCount: Int = 0,
    val totalLogs: Int = 0,
)

private data class Core(val advanced: Boolean, val logs: List<app.vigil.core.data.db.RecoveryLogEntity>, val sessions: List<app.vigil.core.data.db.WatchSessionEntity>)
private data class Extra(
    val readings: List<app.vigil.core.data.db.BibleReadingEntity>,
    val blocked: Int,
    val allowed: Int,
    val favorites: Int,
)

class DashboardViewModel(private val c: AppContainer) : ViewModel() {
    private val core = combine(c.settings.advancedDashboard, c.recovery.logs, c.watch.sessions) { adv, logs, sessions ->
        Core(adv, logs, sessions)
    }
    private val extra = combine(c.devotion.readings, c.shield.blocked, c.shield.allowed, c.devotion.favoriteKeys) { r, b, a, f ->
        Extra(r, b.size, a.size, f.size)
    }

    val state: StateFlow<DashboardState> = combine(core, extra) { co, ex ->
        val now = System.currentTimeMillis()
        val w7 = now - 7 * DAY_MS
        val w30 = now - 30 * DAY_MS
        val readKeys = ex.readings.map { it.readingKey }.toSet()
        DashboardState(
            advanced = co.advanced,
            recoveryLogs7d = co.logs.count { it.timestamp >= w7 },
            watchCompleted7d = co.sessions.count { it.startedAt >= w7 && it.completed },
            readings7d = ex.readings.count { it.readAt >= w7 },
            nextReading = DevotionContent.readings.firstOrNull { it.key !in readKeys }
                ?: DevotionContent.readings.firstOrNull(),
            urges30d = co.logs.count { it.timestamp >= w30 && it.kind == RecoveryKind.URGE.name },
            lapses30d = co.logs.count { it.timestamp >= w30 && it.kind == RecoveryKind.LAPSE.name },
            blockedCount = ex.blocked,
            allowedCount = ex.allowed,
            favoritesCount = ex.favorites,
            totalLogs = co.logs.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    fun setAdvanced(value: Boolean) {
        viewModelScope.launch { c.settings.setAdvancedDashboard(value) }
    }
}

private data class ModuleEntry(val name: String, val status: String, val route: String)

private val modules = listOf(
    ModuleEntry("Shield", "listas prontas · filtro pendente", Routes.SHIELD),
    ModuleEntry("Watch", "pausas manuais · alertas pendentes", Routes.WATCH),
    ModuleEntry("Recovery", "registro local", Routes.RECOVERY),
    ModuleEntry("Devotion", "leituras", Routes.DEVOTION),
    ModuleEntry("Intelligence", "planejado", Routes.INTELLIGENCE),
    ModuleEntry("Vision", "planejado", Routes.VISION),
)

@Composable
fun DashboardScreen(onOpen: (String) -> Unit, onOpenReading: (String) -> Unit) {
    val vm = vigilViewModel { DashboardViewModel(it) }
    val s by vm.state.collectAsStateWithLifecycle()

    VigilScreen("VIGIL", onBack = null) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                "Vigie. Resista. Persevere.",
                style = MaterialTheme.typography.labelMedium,
                color = VigilColors.Muted,
            )

            SectionLabel("Estado")
            KeyValueRow("Proteção", "INATIVA · filtro DNS na Fase 2", VigilColors.Warning, onClick = { onOpen(Routes.SHIELD) })
            KeyValueRow("Watch", "SEM ALERTAS · notificações na Fase 3", VigilColors.Warning, onClick = { onOpen(Routes.WATCH) })
            Hairline()

            SectionLabel("Atividade · 7 dias")
            if (s.recoveryLogs7d + s.watchCompleted7d + s.readings7d == 0) {
                EmptyState("Nenhuma atividade registrada nos últimos 7 dias.")
            } else {
                KeyValueRow("Registros de recuperação", s.recoveryLogs7d.toString())
                KeyValueRow("Pausas concluídas", s.watchCompleted7d.toString())
                KeyValueRow("Leituras", s.readings7d.toString())
            }
            Hairline()

            SectionLabel("Próxima leitura")
            val next = s.nextReading
            if (next == null) {
                EmptyState("Sem leituras disponíveis.")
            } else {
                KeyValueRow(next.reference, next.title, VigilColors.Green, onClick = { onOpenReading(next.key) })
            }
            Hairline()

            SectionLabel("Módulos")
            modules.forEach { m ->
                KeyValueRow(m.name, m.status, VigilColors.Muted, onClick = { onOpen(m.route) })
            }
            Hairline()

            SectionLabel("Ajustes rápidos")
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Painel avançado", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = s.advanced, onCheckedChange = vm::setAdvanced)
            }
            OutlinedButton(onClick = { onOpen(Routes.SETTINGS) }) { Text("Configurações") }

            if (s.advanced) {
                Hairline()
                SectionLabel("Avançado")
                KeyValueRow("Domínios bloqueados", s.blockedCount.toString())
                KeyValueRow("Domínios permitidos", s.allowedCount.toString())
                KeyValueRow("Vontades · 30 dias", s.urges30d.toString())
                KeyValueRow("Recaídas registradas · 30 dias", s.lapses30d.toString())
                KeyValueRow("Registros totais", s.totalLogs.toString())
                KeyValueRow("Leituras favoritas", s.favoritesCount.toString())
            }
            Column(Modifier.padding(bottom = 24.dp)) {}
        }
    }
}
