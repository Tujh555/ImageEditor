package org.example.project.presentation.image.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import org.example.project.presentation.compose.DEFAULT_HORIZONTAL_PADDING_DP
import org.example.project.presentation.compose.constrained
import org.example.project.presentation.models.ImageUiModel

private val minCellSize = 64.dp

@Composable
internal fun ImageListScreenContent(
    state: ImageListScreen.State,
    onAction: (ImageListScreen.Action) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .constrained()
    ) {
        ImageGrid(
            modifier = Modifier.fillMaxSize(),
            imagesMap = state.imagesDateMap
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun ImageGrid(
    modifier: Modifier = Modifier,
    imagesMap: Map<String, List<ImageUiModel>>
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cellSize = (screenWidth - DEFAULT_HORIZONTAL_PADDING_DP.dp) / 3

    LazyColumn(
        modifier = modifier
    ) {
        imagesMap.forEach { (date, images) ->
            stickyHeader {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}