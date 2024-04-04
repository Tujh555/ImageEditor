package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.example.project.presentation.screen.navigation.host.Host
import org.example.project.presentation.screen.navigation.router.GlobalRouter

private val host = Host().apply {
    GlobalRouter.init(this)
}

internal class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                val stack by host.screenStack.collectAsState()

                val topScreen = stack.lastOrNull()

                AnimatedContent(
                    targetState = topScreen,
                    modifier = Modifier.fillMaxSize(),
                    label = "screen_stack",
                    transitionSpec = {
                        val animationSpec = spring<Float>(stiffness = Spring.StiffnessMediumLow)
                        fadeIn(animationSpec) togetherWith fadeOut(animationSpec)
                    },
                    contentKey = { topScreen?.key },
                ) {
                    it?.Content()
                }
            }
        }
    }
}