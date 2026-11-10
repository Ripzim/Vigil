package app.vigil.feature.shield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import app.vigil.core.data.DomainResult
import app.vigil.core.data.ShieldRepository
import app.vigil.core.di.vigilViewModel
import app.vigil.core.ui.EmptyState
import app.vigil.core.ui.Hairline
import app.vigil.core.ui.SectionLabel
import app.vigil.core.ui.StatusBanner
import app.vigil.core.ui.VigilScreen
import app.vigil.core.ui.theme.VigilColors
import app.vigil.core.util.formatDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShieldViewModel(private val repo: ShieldRepository) : ViewModel() {
    val blocked = repo.blocked.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allowed = repo.allowed.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val events = repo.events.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun add(raw: String, toBlocklist: Boolean, onAdded: () -> Unit) {
        viewModelScope.launch {
            val result = if (toBlocklist) repo.addBlocked(raw) else repo.addAllowed(raw)
            _message.value = when (result) {
                DomainResult.ADDED -> null
                DomainResult.DUPLICATE -> "Este domínio já está na lista."
                DomainResult.INVALID -> "Domínio inválido. Exemplo: exemplo.com"
            }
            if (result == DomainResult.ADDED) onAdded()
        }
    }

    fun remove(id: Long, fromBlocklist: Boolean) {
        viewModelScope.launch { if (fromBlocklist) repo.removeBlocked(id) else repo.removeAllowed(id) }
    }

    fun clearMessage() {
        _message.value = null
    }
}

@Composable
fun ShieldScreen(onBack: () -> Unit) {
    val vm = vigilViewModel { ShieldViewModel(it.shield) }
    val blocked by vm.blocked.collectAsStateWithLifecycle()
    val allowed by vm.allowed.collectAsStateWithLifecycle()
    val events by vm.events.collectAsStateWithLifecycle()
    val message by vm.message.collectAsStateWithLifecycle()

    var tab by remember { mutableIntStateOf(0) }
    var input by remember { mutableStateOf("") }
    val onBlocklist = tab == 0

    VigilScreen("Shield", onBack) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item {
                StatusBanner(
                    "PROTEÇÃO INATIVA — o filtro DNS (VpnService) ainda não foi implementado. " +
                        "As listas abaixo são salvas, mas nada é bloqueado nesta versão.",
                )
            }
            item {
                TabRow(
                    selectedTabIndex = tab,
                    containerColor = Color.Black,
                    contentColor = VigilColors.Green,
                ) {
                    Tab(selected = onBlocklist, onClick = { tab = 0; input = ""; vm.clearMessage() }, text = { Text("Bloqueados (${blocked.size})") })
                    Tab(selected = !onBlocklist, onClick = { tab = 1; input = ""; vm.clearMessage() }, text = { Text("Permitidos (${allowed.size})") })
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it; vm.clearMessage() },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("domínio, ex.: exemplo.com") },
                        isError = message != null,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    )
                    Button(onClick = { vm.add(input, onBlocklist) { input = "" } }) { Text("Adicionar") }
                }
            }
            message?.let { msg ->
                item { Text(msg, style = MaterialTheme.typography.bodySmall, color = VigilColors.Error) }
            }

            if (onBlocklist) {
                if (blocked.isEmpty()) item { EmptyState("Nenhum domínio bloqueado.") }
                items(blocked, key = { "b${it.id}" }) { d ->
                    DomainRow(d.domain) { vm.remove(d.id, true) }
                }
            } else {
                if (allowed.isEmpty()) item { EmptyState("Nenhum domínio permitido.") }
                items(allowed, key = { "a${it.id}" }) { d ->
                    DomainRow(d.domain) { vm.remove(d.id, false) }
                }
            }

            item { Hairline() }
            item { SectionLabel("Eventos de proteção") }
            if (events.isEmpty()) {
                item { EmptyState("Nenhum evento. O histórico guarda apenas tipo e horário, nunca URLs visitadas.") }
            }
            items(events, key = { "e${it.id}" }) { e ->
                Text(
                    "${e.timestamp.formatDateTime()}  ${e.type}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VigilColors.Muted,
                )
            }
        }
    }
}

@Composable
private fun DomainRow(domain: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(domain, style = MaterialTheme.typography.bodyMedium)
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Remover $domain", tint = VigilColors.Muted)
        }
    }
}
