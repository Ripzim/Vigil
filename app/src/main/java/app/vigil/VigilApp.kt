package app.vigil

import android.app.Application
import app.vigil.core.di.AppContainer

class VigilApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
