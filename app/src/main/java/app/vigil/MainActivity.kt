package app.vigil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.vigil.core.ui.theme.VigilTheme
import app.vigil.navigation.VigilNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            VigilTheme {
                VigilNavHost()
            }
        }
    }
}
