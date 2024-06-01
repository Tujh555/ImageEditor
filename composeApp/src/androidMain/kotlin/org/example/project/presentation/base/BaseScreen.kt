package org.example.project.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen

abstract class BaseScreen<A, S, E, M : BaseScreenModel<A ,S, E>> : Screen {

    @Composable
    final override fun Content() {
        val screenModel = getModel()
        val state by screenModel.state.collectAsState()
        val event by screenModel.event.collectAsState(null)

        Content(state = state, onAction = screenModel::onAction)
        event?.let { Event(it) }
    }

    @Composable
    abstract fun Content(state: S, onAction: (A) -> Unit)

    @Composable
    abstract fun Event(event: E)

    @Composable
    abstract fun getModel(): M
}