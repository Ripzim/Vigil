package app.vigil.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import app.vigil.core.di.AppContainer
import app.vigil.core.di.vigilViewModel
import app.vigil.core.ui.Hairline
import app.vigil.core.ui.KeyValueRow
import app.vigil.core.ui.SectionLabel
import app.vigil.core.ui.VigilScreen
import app.vigil.core.ui.theme.VigilColors
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val c: AppContainer) : ViewModel() {
    val advanced = c.settings.advancedDashboard.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setAdvanced(value: Boolean) {
        viewModelScope.launch { c.settings.setAdvancedDashboard(value) }
    }

    fun wipe(onDone: () -> Unit) {
        viewModelScope.launch {
            c.wipeAllData()
            onDone()
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val vm = vigilViewModel { SettingsViewModel(it) }
    val advanced by vm.advanced.collectAsStateWithLifecycle()
    var confirmWipe by remember { mutableStateOf(false) }
    var wiped by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val version = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: "?"
    }

    VigilScreen("Configurações", onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionLabel("Painel")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Painel avançado", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = advanced, onCheckedChange = vm::setAdvanced)
            }
            Hairline()

            SectionLabel("Privacidade")
            Text(
                "Todos os dados ficam somente neste aparelho. O app não tem contas, telemetria, anúncios nem " +
                    "sincronização, e nesta versão não declara permissão de internet. O backup do Android " +
                    "também está desativado para os dados do VIGIL.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Limite técnico: os dados do banco ainda não são criptografados em repouso. " +
                    "A proteção com Android Keystore está planejada para a Fase 2.",
                style = MaterialTheme.typography.bodySmall,
                color = VigilColors.Warning,
            )
            Hairline()

            SectionLabel("Dados")
            Text(
                "Apaga todos os registros, listas e configurações do VIGIL neste aparelho. Não pode ser desfeito.",
                style = MaterialTheme.typography.bodySmall,
                color = VigilColors.Muted,
            )
            Button(
                onClick = { confirmWipe = true },
                colors = ButtonDefaults.buttonColors(containerColor = VigilColors.Error, contentColor = VigilColors.Black),
            ) { Text("Apagar todos os meus dados") }
            if (wiped) {
                Text("Dados apagados.", style = MaterialTheme.typography.bodyMedium, color = VigilColors.Green)
            }
            Hairline()

            SectionLabel("Sobre")
            KeyValueRow("Versão", version, VigilColors.Muted)
            KeyValueRow("Plataforma", "Android nativo", VigilColors.Muted)
            Column(Modifier.padding(bottom = 24.dp)) {}
        }
    }

    if (confirmWipe) {
        AlertDialog(
            onDismissRequest = { confirmWipe = false },
            title = { Text("Apagar todos os dados?") },
            text = { Text("Registros, listas de domínios, histórico de leitura, favoritos e configurações serão removidos permanentemente.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmWipe = false
                    vm.wipe { wiped = true }
                }) { Text("Apagar", color = VigilColors.Error) }
            },
            dismissButton = { TextButton(onClick = { confirmWipe = false }) { Text("Cancelar") } },
        )
    }
}
