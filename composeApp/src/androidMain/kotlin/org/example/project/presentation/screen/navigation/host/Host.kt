package org.example.project.presentation.screen.navigation.host

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.presentation.screen.navigation.ScreenTransformation
import org.example.project.presentation.screen.navigation.router.TransformationSupplier
import org.example.project.presentation.screen.navigation.screen.Screen

class Host(initialScreen: Screen? = null) : TransformationSupplier {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val queue = Channel<ScreenTransformation>(Channel.UNLIMITED)
    private val _screenStack = MutableStateFlow(listOfNotNull(initialScreen))
    val screenStack = _screenStack.asStateFlow()

    init {
        coroutineScope.launch {
            for (transformation in queue) {
                _screenStack.update(transformation)
            }
        }
    }

    override fun transform(transformation: ScreenTransformation) {
        queue.trySend(transformation)
    }
}