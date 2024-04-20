package org.example.project.editor

import androidx.compose.ui.graphics.Canvas

typealias ComposeCanvas = Canvas
typealias AndroidCanvas = android.graphics.Canvas

fun AndroidCanvas.asCompose() = Canvas(this)