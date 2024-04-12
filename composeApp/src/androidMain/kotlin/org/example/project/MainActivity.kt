package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import api.CommonApiHolder
import implementation.domain.models.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID

internal class MainActivity : ComponentActivity() {
    private val repository = CommonApiHolder.get().repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(5000)
            repository.insert(
                Image(
                    id = UUID.randomUUID().toString(),
                    name = "AAAAAAAAA жопа",
                    saveDate = Clock.System.now(),
                    path = "empty"
                )
            )
        }

        setContent {
            MaterialTheme {
                val images by repository.list.collectAsState()
                val boxShape = remember {
                    RoundedCornerShape(8.dp)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = remember {
                        PaddingValues(horizontal = 16.dp)
                    },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(
                        items = images,
                        key = { it.id },
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(boxShape)
                                .background(
                                    color = MaterialTheme.colors.secondary,
                                    shape = boxShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Name: ${it.name}")
                        }
                    }
                }
            }
        }
    }
}