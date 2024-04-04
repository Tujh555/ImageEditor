package org.example.project.presentation.screen.navigation.screen

import androidx.compose.runtime.Composable

interface Screen {
    val key: Any

    @Composable
    fun Content()
}