package app.vigil.feature.devotion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import app.vigil.core.data.DevotionRepository
import app.vigil.core.di.vigilViewModel
import app.vigil.core.ui.EmptyState
import app.vigil.core.ui.Hairline
import app.vigil.core.ui.SectionLabel
import app.vigil.core.ui.VigilScreen
import app.vigil.core.ui.theme.VigilColors
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DevotionViewModel(private val repo: DevotionRepository) : ViewModel() {
    val readCounts: StateFlow<Map<String, Int>> = repo.readings
        .map { list -> list.groupingBy { it.readingKey }.eachCount() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val favorites: StateFlow<Set<String>> = repo.favoriteKeys
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun markRead(key: String) {
        viewModelScope.launch { repo.markRead(key) }
    }

    fun setFavorite(key: String, favorite: Boolean) {
        viewModelScope.launch { repo.setFavorite(key, favorite) }
    }
}

@Composable
fun DevotionListScreen(onBack: () -> Unit, onOpenReading: (String) -> Unit) {
    val vm = vigilViewModel { DevotionViewModel(it.devotion) }
    val counts by vm.readCounts.collectAsStateWithLifecycle()
    val favorites by vm.favorites.collectAsStateWithLifecycle()
    var onlyFavorites by remember { mutableStateOf(false) }
    val visible = DevotionContent.readings.filter { !onlyFavorites || it.key in favorites }

    VigilScreen("Devotion", onBack) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item {
                Text(
                    DevotionContent.TRANSLATION_NOTE,
                    style = MaterialTheme.typography.bodySmall,
                    color = VigilColors.Muted,
                )
            }
            item {
                FilterChip(
                    selected = onlyFavorites,
                    onClick = { onlyFavorites = !onlyFavorites },
                    label = { Text("Somente favoritos") },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            if (visible.isEmpty()) {
                item { EmptyState("Nenhum favorito ainda. Abra uma leitura e marque como favorita.") }
            }
            items(visible, key = { it.key }) { reading ->
                Column {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenReading(reading.key) }
                            .padding(vertical = 10.dp),
                    ) {
                        Text(reading.reference, style = MaterialTheme.typography.titleMedium, color = VigilColors.Green)
                        Text(reading.title, style = MaterialTheme.typography.bodyMedium)
                        val n = counts[reading.key] ?: 0
                        val fav = if (reading.key in favorites) "favorita" else null
                        val read = if (n > 0) "lida $n×" else "não lida"
                        Text(
                            listOfNotNull(read, fav).joinToString(" · "),
                            style = MaterialTheme.typography.labelSmall,
                            color = VigilColors.Muted,
                        )
                    }
                    Hairline()
                }
            }
        }
    }
}

@Composable
fun DevotionDetailScreen(key: String, onBack: () -> Unit) {
    val vm = vigilViewModel { DevotionViewModel(it.devotion) }
    val counts by vm.readCounts.collectAsStateWithLifecycle()
    val favorites by vm.favorites.collectAsStateWithLifecycle()
    val reading = DevotionContent.byKey(key)

    VigilScreen("Leitura", onBack) { padding ->
        if (reading == null) {
            Column(Modifier.padding(padding).padding(16.dp)) { EmptyState("Leitura não encontrada.") }
            return@VigilScreen
        }
        val isFavorite = reading.key in favorites
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(reading.reference, style = MaterialTheme.typography.headlineMedium, color = VigilColors.Green)
            Text(reading.title, style = MaterialTheme.typography.titleMedium)
            Text(
                DevotionContent.TRANSLATION_NOTE,
                style = MaterialTheme.typography.bodySmall,
                color = VigilColors.Muted,
            )
            SectionLabel("Contexto")
            Text(reading.context, style = MaterialTheme.typography.bodyMedium)
            SectionLabel("Reflexão")
            Text(reading.reflection, style = MaterialTheme.typography.bodyMedium)
            SectionLabel("Oração")
            Text(reading.prayer, style = MaterialTheme.typography.bodyMedium)
            Hairline()
            val n = counts[reading.key] ?: 0
            Text(
                if (n > 0) "Marcada como lida $n vez(es)." else "Ainda não marcada como lida.",
                style = MaterialTheme.typography.labelSmall,
                color = VigilColors.Muted,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { vm.markRead(reading.key) }) { Text("Marcar como lida") }
                OutlinedButton(onClick = { vm.setFavorite(reading.key, !isFavorite) }) {
                    Text(if (isFavorite) "Remover favorito" else "Favoritar")
                }
            }
            Column(Modifier.padding(bottom = 24.dp)) {}
        }
    }
}
