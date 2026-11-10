package app.vigil.feature.recovery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import app.vigil.core.data.RecoveryRepository
import app.vigil.core.data.db.RecoveryKind
import app.vigil.core.di.vigilViewModel
import app.vigil.core.ui.EmptyState
import app.vigil.core.ui.Hairline
import app.vigil.core.ui.KeyValueRow
import app.vigil.core.ui.SectionLabel
import app.vigil.core.ui.VigilScreen
import app.vigil.core.ui.theme.VigilColors
import app.vigil.core.util.DAY_MS
import app.vigil.core.util.formatDateTime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecoveryViewModel(private val repo: RecoveryRepository) : ViewModel() {
    val logs = repo.logs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val triggers = repo.triggers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun save(kind: RecoveryKind, trigger: String?, intensity: Int, note: String?, onSaved: () -> Unit) {
        viewModelScope.launch {
            repo.add(kind, trigger, intensity, note)
            onSaved()
        }
    }

    fun addTrigger(name: String, onAdded: () -> Unit) {
        viewModelScope.launch { if (repo.addTrigger(name)) onAdded() }
    }

    fun deleteLog(id: Long) {
        viewModelScope.launch { repo.deleteLog(id) }
    }
}

private fun kindLabel(kind: String) = if (kind == RecoveryKind.LAPSE.name) "Recaída" else "Vontade"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecoveryScreen(onBack: () -> Unit) {
    val vm = vigilViewModel { RecoveryViewModel(it.recovery) }
    val logs by vm.logs.collectAsStateWithLifecycle()
    val triggers by vm.triggers.collectAsStateWithLifecycle()

    var kind by remember { mutableStateOf(RecoveryKind.URGE) }
    var intensity by remember { mutableFloatStateOf(3f) }
    var selectedTrigger by remember { mutableStateOf<String?>(null) }
    var newTrigger by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val since = remember { System.currentTimeMillis() - 30 * DAY_MS }
    val recent = logs.filter { it.timestamp >= since }
    val urges = recent.count { it.kind == RecoveryKind.URGE.name }
    val lapses = recent.count { it.kind == RecoveryKind.LAPSE.name }
    val topTriggers = recent.mapNotNull { it.triggerName }
        .groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(3)

    VigilScreen("Recovery", onBack) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    "Os registros ficam apenas neste aparelho e servem para você enxergar padrões, não para contar culpa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = VigilColors.Muted,
                )
            }
            item { SectionLabel("Registrar") }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = kind == RecoveryKind.URGE,
                        onClick = { kind = RecoveryKind.URGE },
                        label = { Text("Vontade") },
                    )
                    FilterChip(
                        selected = kind == RecoveryKind.LAPSE,
                        onClick = { kind = RecoveryKind.LAPSE },
                        label = { Text("Recaída") },
                    )
                }
            }
            item {
                Column {
                    Text(
                        "Intensidade: ${intensity.toInt()} de 5",
                        style = MaterialTheme.typography.bodySmall,
                        color = VigilColors.Muted,
                    )
                    Slider(value = intensity, onValueChange = { intensity = it }, valueRange = 1f..5f, steps = 3)
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Gatilho (opcional)", style = MaterialTheme.typography.bodySmall, color = VigilColors.Muted)
                    if (triggers.isEmpty()) {
                        EmptyState("Nenhum gatilho cadastrado. Adicione abaixo (ex.: tédio, cansaço, solidão).")
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        triggers.forEach { t ->
                            FilterChip(
                                selected = selectedTrigger == t.name,
                                onClick = { selectedTrigger = if (selectedTrigger == t.name) null else t.name },
                                label = { Text(t.name) },
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = newTrigger,
                            onValueChange = { newTrigger = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            label = { Text("novo gatilho") },
                        )
                        OutlinedButton(onClick = { vm.addTrigger(newTrigger) { newTrigger = "" } }) { Text("Adicionar") }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nota (opcional)") },
                    minLines = 2,
                )
            }
            item {
                Button(onClick = {
                    val savedKind = kind
                    vm.save(savedKind, selectedTrigger, intensity.toInt(), note) {
                        note = ""
                        selectedTrigger = null
                        message = if (savedKind == RecoveryKind.LAPSE) {
                            "Registrado. Este momento não define quem você é em Cristo. " +
                                "Sugestão: leia 1 João 1:5 a 2:2 em Devotion e recomece hoje."
                        } else {
                            "Registrado. Reconhecer a vontade já é vigiar. Tente uma pausa consciente no Watch."
                        }
                    }
                }) { Text("Salvar registro") }
            }
            message?.let { msg ->
                item { Text(msg, style = MaterialTheme.typography.bodyMedium, color = VigilColors.Green) }
            }

            item { Hairline() }
            item { SectionLabel("Resumo local · 30 dias") }
            item {
                if (recent.isEmpty()) {
                    EmptyState("Sem registros no período.")
                } else {
                    Column {
                        KeyValueRow("Vontades registradas", urges.toString())
                        KeyValueRow("Recaídas registradas", lapses.toString())
                        if (topTriggers.isNotEmpty()) {
                            KeyValueRow(
                                "Gatilhos mais frequentes",
                                topTriggers.joinToString(", ") { "${it.key} (${it.value})" },
                                VigilColors.Muted,
                            )
                        }
                    }
                }
            }

            item { Hairline() }
            item { SectionLabel("Histórico") }
            if (logs.isEmpty()) item { EmptyState("Nenhum registro ainda.") }
            items(logs, key = { it.id }) { log ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "${log.timestamp.formatDateTime()} · ${kindLabel(log.kind)} · intensidade ${log.intensity}",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        val detail = listOfNotNull(log.triggerName?.let { "gatilho: $it" }, log.note).joinToString(" — ")
                        if (detail.isNotEmpty()) {
                            Text(detail, style = MaterialTheme.typography.bodySmall, color = VigilColors.Muted)
                        }
                    }
                    IconButton(onClick = { vm.deleteLog(log.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Apagar registro", tint = VigilColors.Muted)
                    }
                }
            }
        }
    }
}
