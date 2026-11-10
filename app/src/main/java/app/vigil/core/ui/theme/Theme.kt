package app.vigil.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

object VigilColors {
    val Black = Color(0xFF000000)
    val Green = Color(0xFF4CAF7A)
    val GreenDim = Color(0xFF2E6B4A)
    val Text = Color(0xFFE6E6E6)
    val Muted = Color(0xFF8A8A8A)
    val Outline = Color(0xFF262626)
    val SurfaceVariant = Color(0xFF0D0D0D)
    val Warning = Color(0xFFD9A441)
    val Error = Color(0xFFE57373)
}

private val scheme = darkColorScheme(
    primary = VigilColors.Green,
    onPrimary = VigilColors.Black,
    secondary = VigilColors.GreenDim,
    background = VigilColors.Black,
    onBackground = VigilColors.Text,
    surface = VigilColors.Black,
    onSurface = VigilColors.Text,
    surfaceVariant = VigilColors.SurfaceVariant,
    onSurfaceVariant = VigilColors.Muted,
    outline = VigilColors.Outline,
    outlineVariant = VigilColors.Outline,
    error = VigilColors.Error,
)

private val base = Typography()

/** Títulos e rótulos em monoespaçada (aparência de terminal); texto corrido em fonte padrão. */
private val typography = base.copy(
    headlineMedium = base.headlineMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium),
    titleLarge = base.titleLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium),
    titleMedium = base.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium),
    labelLarge = base.labelLarge.copy(fontFamily = FontFamily.Monospace),
    labelMedium = base.labelMedium.copy(fontFamily = FontFamily.Monospace),
    labelSmall = base.labelSmall.copy(fontFamily = FontFamily.Monospace),
)

@Composable
fun VigilTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, typography = typography, content = content)
}
