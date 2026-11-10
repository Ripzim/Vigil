package app.vigil.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Tela de módulo ainda não implementado: informa o estado real, sem botões decorativos. */
@Composable
fun ModuleInfoScreen(
    title: String,
    status: String,
    description: String,
    plannedItems: List<String>,
    constraints: List<String>,
    onBack: () -> Unit,
) {
    VigilScreen(title, onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusBanner(status)
            Text(description, style = MaterialTheme.typography.bodyMedium)
            SectionLabel("Planejado")
            plannedItems.forEach { Text("› $it", style = MaterialTheme.typography.bodyMedium) }
            SectionLabel("Princípios")
            constraints.forEach { Text("› $it", style = MaterialTheme.typography.bodyMedium) }
            Column(Modifier.padding(bottom = 24.dp)) {}
        }
    }
}
