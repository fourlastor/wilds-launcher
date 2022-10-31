package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.fourlastor.wilds_launcher.Context
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun Downloader(context: Context, saveWildsDir: (String, String) -> Unit, onError: () -> Unit) {
    val startDownload = remember { mutableStateOf(true) }
    val progress = remember { mutableStateOf(0f) }

    if (startDownload.value) {
        startDownload.value = false

        context.coroutineScope.launch {
            val rootDirectory = context.releaseService.downloadLatestRelease { progress.value = it }

            if (rootDirectory == null) {
                onError()
                return@launch
            }

            val gameDirectory = rootDirectory.walk().drop(1).first()
            val appDirectory = gameDirectory.absolutePath + File.separator + "app"

            saveWildsDir(appDirectory, FILENAME)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text("Downloading...")
            LinearProgressIndicator(progress.value)
        }
    }
}