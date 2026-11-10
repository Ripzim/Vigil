package app.vigil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.vigil.feature.dashboard.DashboardScreen
import app.vigil.feature.devotion.DevotionDetailScreen
import app.vigil.feature.devotion.DevotionListScreen
import app.vigil.feature.intelligence.IntelligenceScreen
import app.vigil.feature.recovery.RecoveryScreen
import app.vigil.feature.settings.SettingsScreen
import app.vigil.feature.shield.ShieldScreen
import app.vigil.feature.vision.VisionScreen
import app.vigil.feature.watch.WatchScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val SHIELD = "shield"
    const val WATCH = "watch"
    const val RECOVERY = "recovery"
    const val DEVOTION = "devotion"
    const val DEVOTION_DETAIL = "devotion/{key}"
    const val INTELLIGENCE = "intelligence"
    const val VISION = "vision"
    const val SETTINGS = "settings"

    fun devotionDetail(key: String) = "devotion/$key"
}

@Composable
fun VigilNavHost() {
    val nav = rememberNavController()
    val back: () -> Unit = { nav.popBackStack() }

    NavHost(navController = nav, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onOpen = { route -> nav.navigate(route) },
                onOpenReading = { key -> nav.navigate(Routes.devotionDetail(key)) },
            )
        }
        composable(Routes.SHIELD) { ShieldScreen(onBack = back) }
        composable(Routes.WATCH) { WatchScreen(onBack = back) }
        composable(Routes.RECOVERY) { RecoveryScreen(onBack = back) }
        composable(Routes.DEVOTION) {
            DevotionListScreen(onBack = back, onOpenReading = { key -> nav.navigate(Routes.devotionDetail(key)) })
        }
        composable(
            route = Routes.DEVOTION_DETAIL,
            arguments = listOf(navArgument("key") { type = NavType.StringType }),
        ) { entry ->
            DevotionDetailScreen(key = entry.arguments?.getString("key").orEmpty(), onBack = back)
        }
        composable(Routes.INTELLIGENCE) { IntelligenceScreen(onBack = back) }
        composable(Routes.VISION) { VisionScreen(onBack = back) }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = back) }
    }
}
