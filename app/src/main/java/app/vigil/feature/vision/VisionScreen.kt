package app.vigil.feature.vision

import androidx.compose.runtime.Composable
import app.vigil.core.ui.ModuleInfoScreen

@Composable
fun VisionScreen(onBack: () -> Unit) {
    ModuleInfoScreen(
        title = "Vision",
        status = "NÃO IMPLEMENTADO — protótipo independente previsto para a Fase 4. Nada é analisado nesta versão.",
        description = "Módulo independente de análise visual local. Antes de qualquer integração, será feito um " +
            "protótipo para medir desempenho e precisão no aparelho. Não há promessa de monitorar outros aplicativos.",
        plannedItems = listOf(
            "Classificação local: seguro, sugestivo, nudez, conteúdo sexual explícito",
            "Runtime candidato: LiteRT ou ONNX Runtime",
            "Revisão e correção de falsos positivos",
            "Captura apenas por mecanismos autorizados pelo Android, com consentimento explícito",
        ),
        constraints = listOf(
            "Não salvar capturas de tela",
            "Não transmitir imagens pela internet",
            "Sem vigilância escondida e sem Accessibility Service",
            "Sem bloqueio automático quando a confiança for baixa",
        ),
        onBack = onBack,
    )
}
