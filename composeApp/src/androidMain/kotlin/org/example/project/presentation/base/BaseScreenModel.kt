package org.example.project.presentation.base

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseScreenModel<A, S>(
    initialState: S
) : ScreenModel {
    protected val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    protected val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    protected val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    abstract fun onAction(action: A)

    override fun onDispose() {
        super.onDispose()
        ioScope.cancel()
        mainScope.cancel()
    }
}