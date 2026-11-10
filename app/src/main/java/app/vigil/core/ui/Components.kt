package app.vigil.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.vigil.core.ui.theme.VigilColors

/** Estrutura padrão de tela: barra superior preta e botão de voltar opcional. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VigilScreen(
    title: String,
    onBack: (() -> Unit)?,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = VigilColors.Text,
                    navigationIconContentColor = VigilColors.Text,
                ),
            )
        },
        content = content,
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = "[ ${text.uppercase()} ]",
        style = MaterialTheme.typography.labelMedium,
        color = VigilColors.Green,
        modifier = modifier.padding(top = 8.dp),
    )
}

@Composable
fun KeyValueRow(label: String, value: String, valueColor: Color = VigilColors.Text, onClick: (() -> Unit)? = null) {
    val rowModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = VigilColors.Text)
        Text(value, style = MaterialTheme.typography.labelMedium, color = valueColor)
    }
}

@Composable
fun EmptyState(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = VigilColors.Muted,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
fun Hairline() {
    HorizontalDivider(color = VigilColors.Outline)
}

/** Aviso de estado do módulo, usado para deixar claro o que ainda não está implementado. */
@Composable
fun StatusBanner(text: String, color: Color = VigilColors.Warning) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Hairline()
    }
}
