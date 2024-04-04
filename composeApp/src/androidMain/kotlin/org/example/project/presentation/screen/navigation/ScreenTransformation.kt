package org.example.project.presentation.screen.navigation

import org.example.project.presentation.screen.navigation.screen.Screen
import java.util.concurrent.atomic.AtomicInteger

typealias ScreenTransformation = (List<Screen>) -> List<Screen>

private val integer = AtomicInteger()

fun Any.uniqueKey() = this::class.qualifiedName ?: integer.getAndIncrement().toString()