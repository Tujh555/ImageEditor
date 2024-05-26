package org.example.project.presentation.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal const val DEFAULT_HORIZONTAL_PADDING_DP = 16

@Stable
internal fun Modifier.constrained() = this.padding(horizontal = DEFAULT_HORIZONTAL_PADDING_DP.dp)