package app.vigil.feature.watch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import app.vigil.core.data.WatchRepository
import app.vigil.core.di.vigilViewModel
import app.vigil.core.ui.EmptyState
import app.vigil.core.ui.Hairline
import app.vigil.core.ui.SectionLabel
import app.vigil.core.ui.StatusBanner
import app.vigil.core.ui.VigilScreen
import app.vigil.core.ui.theme.VigilColors
import app.vigil.core.util.formatDateTime
import app.vigil.feature.devotion.DevotionContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchViewModel(private val repo: WatchRepository) : ViewModel() {
    val sessions = repo.sessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun record(startedAt: Long, durationSec: Int, completed: Boolean, activity: String) {
        viewModelScope.launch { repo.record(startedAt, durationSec, completed, activity) }
    }
}

/** Sugestões de atividades alternativas (conteúdo fixo do app, não dados do usuário). */
private val alternativeActivities = listOf(
    "Levante-se e beba um copo de água.",
    "Saia do cômodo e caminhe por alguns minutos.",
    "Faça algumas flexões ou alongamentos.",
    "Ligue ou envie mensagem a alguém de confiança.",
    "Ore em voz alta, nomeando o que você está sentindo.",
    "Lave o rosto e respire fundo dez vezes.",
)

private enum class Phase { IDLE, RUNNING }

@Composable
fun WatchScreen(onBack: () -> Unit) {
    val vm = vigilViewModel { WatchViewModel(it.watch) }
    val sessions by vm.sessions.collectAsStateWithLifecycle()

    var phase by remember { mutableStateOf(Phase.IDLE) }
    var chosenSec by remember { mutableIntStateOf(180) }
    var remaining by remember { mutableIntStateOf(0) }
    var startedAt by remember { mutableLongStateOf(0L) }
    var activityIdx by remember { mutableIntStateOf(0) }
    var readingIdx by remember { mutableIntStateOf(0) }
    var lastResult by remember { mutableStateOf<String?>(null) }

    fun finish(completed: Boolean) {
        val elapsed = (chosenSec - remaining).coerceAtLeast(0)
        vm.record(startedAt, if (completed) chosenSec else elapsed, completed, alternativeActivities[activityIdx])
        lastResult = if (completed) "Pausa concluída e registrada." else "Pausa encerrada antes do fim. Registrada sem problema."
        phase = Phase.IDLE
    }

    LaunchedEffect(phase) {
        if (phase == Phase.RUNNING) {
            while (remaining > 0) {
                delay(1000)
                remaining -= 1
            }
            finish(completed = true)
        }
    }

    val reading = DevotionContent.readings[readingIdx % DevotionContent.readings.size]

    VigilScreen("Watch", onBack) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                StatusBanner(
                    "Nesta versão o Watch funciona apenas sob demanda (pausa consciente manual). " +
                        "Alertas automáticos em horários de risco e notificações silenciosas chegam na Fase 3.",
                )
            }
            item { SectionLabel("Pausa consciente") }

            if (phase == Phase.IDLE) {
                item {
                    Column {
                        Text("Duração", style = MaterialTheme.typography.bodySmall, color = VigilColors.Muted)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(60 to "1 min", 180 to "3 min", 300 to "5 min").forEach { (sec, label) ->
                                FilterChip(
                                    selected = chosenSec == sec,
                                    onClick = { chosenSec = sec },
                                    label = { Text(label) },
                                )
                            }
                        }
                    }
                }
                item {
                    Button(onClick = {
                        activityIdx = (System.currentTimeMillis() % alternativeActivities.size).toInt()
                        readingIdx = (System.currentTimeMillis() / 1000 % DevotionContent.readings.size).toInt()
                        startedAt = System.currentTimeMillis()
                        remaining = chosenSec
                        lastResult = null
                        phase = Phase.RUNNING
                    }) { Text("Iniciar pausa") }
                }
                lastResult?.let { msg ->
                    item { Text(msg, style = MaterialTheme.typography.bodyMedium, color = VigilColors.Green) }
                }
            } else {
                item {
                    val m = remaining / 60
                    val s = remaining % 60
                    Text(
                        "%02d:%02d".format(m, s),
                        style = MaterialTheme.typography.headlineMedium,
                        color = VigilColors.Green,
                    )
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Reflexão · ${reading.reference}", style = MaterialTheme.typography.titleMedium)
                        Text(reading.reflection, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Atividade alternativa", style = MaterialTheme.typography.titleMedium)
                        Text(alternativeActivities[activityIdx], style = MaterialTheme.typography.bodyMedium)
                    }
                }
                item {
                    OutlinedButton(onClick = { finish(completed = false) }) { Text("Encerrar pausa") }
                }
            }

            item { Hairline() }
            item { SectionLabel("Histórico") }
            if (sessions.isEmpty()) item { EmptyState("Nenhuma pausa registrada.") }
            items(sessions.take(30), key = { it.id }) { s ->
                val status = if (s.completed) "concluída" else "encerrada antes"
                Text(
                    "${s.startedAt.formatDateTime()}  ${s.durationSec / 60}min${s.durationSec % 60}s  $status",
                    style = MaterialTheme.typography.labelMedium,
                    color = VigilColors.Muted,
                )
            }
        }
    }
}
