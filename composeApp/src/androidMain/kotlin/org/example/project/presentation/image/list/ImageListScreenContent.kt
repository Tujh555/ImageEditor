package org.example.project.presentation.image.list

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.example.project.R
import org.example.project.presentation.models.ImageUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageListScreenContent(
    state: ImageListScreen.State,
    onAction: (ImageListScreen.Action) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Small Top App Bar")
                }
            )
            ImageGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                imagesMap = state.imagesDateMap,
            )
        }

        AddImageButton(
            modifier = Modifier
                .padding(end = 24.dp, bottom = 24.dp)
                .align(Alignment.BottomEnd),
            save = {
                onAction(ImageListScreen.Action.SaveImage(it))
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun ImageGrid(
    modifier: Modifier = Modifier,
    imagesMap: Map<String, List<ImageUiModel>>,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        imagesMap.forEach { (date, images) ->
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            item(key = date) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateItemPlacement(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    images.fastForEach { image ->
                        ImageCell(
                            modifier = Modifier.animateItemPlacement(),
                            image = image
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageCell(
    modifier: Modifier = Modifier,
    image: ImageUiModel,
) {
    val shape = remember {
        RoundedCornerShape(8.dp)
    }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cellSize = screenWidth / 4
    val cellSizePx = with(LocalDensity.current) {
        cellSize.roundToPx()
    }

    Box(
        modifier = modifier
            .size(cellSize)
            .clip(shape)
    ) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .crossfade(true)
                .size(cellSizePx)
                .data(image.path)
                .error(R.drawable.content)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .aspectRatio(1f)
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(color = Color.Black.copy(alpha = 0.5f))
                .padding(4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = image.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AddImageButton(
    modifier: Modifier = Modifier,
    save: (Uri) -> Unit
) {
    val getImageContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let(save)
        }
    )
    FloatingActionButton(
        modifier = modifier.imePadding(),
        onClick = {
            getImageContract.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        content = {
            Icon(Icons.Filled.Add, null)
        }
    )
}