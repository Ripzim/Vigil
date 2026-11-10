package app.vigil.feature.intelligence

import androidx.compose.runtime.Composable
import app.vigil.core.ui.ModuleInfoScreen

@Composable
fun IntelligenceScreen(onBack: () -> Unit) {
    ModuleInfoScreen(
        title = "Intelligence",
        status = "NÃO IMPLEMENTADO — previsto para a Fase 4. Nenhuma análise é executada nesta versão.",
        description = "Inteligência local do VIGIL. A primeira versão será baseada em regras e estatísticas " +
            "calculadas sobre os seus registros, sem IA generativa.",
        plannedItems = listOf(
            "Padrões de horários a partir dos registros de Recovery",
            "Gatilhos mais associados a cada período do dia",
            "Recomendações preventivas explicáveis",
            "Estatísticas com a regra usada visível",
        ),
        constraints = listOf(
            "Processamento 100% local",
            "Sem envio de dados e sem IA online",
            "Sem métricas inventadas: sem dados, sem resultado",
        ),
        onBack = onBack,
    )
}
