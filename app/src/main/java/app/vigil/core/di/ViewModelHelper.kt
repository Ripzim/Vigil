package app.vigil.core.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.vigil.VigilApp

/** Cria um ViewModel recebendo o [AppContainer], sem biblioteca de DI. */
@Composable
inline fun <reified VM : ViewModel> vigilViewModel(crossinline create: (AppContainer) -> VM): VM {
    val container = (LocalContext.current.applicationContext as VigilApp).container
    return viewModel(factory = viewModelFactory { initializer { create(container) } })
}
