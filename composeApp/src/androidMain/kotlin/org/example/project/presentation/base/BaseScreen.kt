package org.example.project.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen

abstract class BaseScreen<A, S, M : BaseScreenModel<A ,S>> : Screen {

    @Composable
    final override fun Content() {
        val screenModel = getModel()
        val state by screenModel.state.collectAsState()

        Content(state = state, onAction = screenModel::onAction)
    }

    @Composable
    abstract fun Content(state: S, onAction: (A) -> Unit)

    @Composable
    abstract fun getModel(): M
}