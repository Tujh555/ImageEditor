import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import data.database.driver.DatabaseFactoryAndroid
import data.repository.TestRepositoryImpl
import data.repository.testRepository
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import imageeditor.composeapp.generated.resources.Res
import imageeditor.composeapp.generated.resources.compose_multiplatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
internal fun AppContent() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var titles by remember {
            mutableStateOf(listOf<String>())
        }

        val cn = LocalContext.current
        val repository = remember {
            TestRepositoryImpl(DatabaseFactoryAndroid(cn))
        }

        LaunchedEffect(key1 = Unit) {
            titles = withContext(Dispatchers.IO) {
                repository.getAllSortedByRating().fastMap { it.title }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showContent = !showContent }
            ) {
                Text("Fuck me!")
            }

            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null
                    )

                    Text(text = "Compose: $greeting")
                }
            }

            titles.fastForEach {
                Text(text = it)
            }
        }
    }
}