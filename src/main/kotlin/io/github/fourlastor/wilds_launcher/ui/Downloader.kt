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

@Composable
fun Downloader(context: Context, onSuccess: () -> Unit, onError: () -> Unit) {
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

            val jarFile = context.releaseService.findInstallation()

            if (jarFile == null) {
                onError()
                return@launch
            }

            context.settingsService.setJar(jarFile.absolutePath)
            onSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text("Downloading...")
            LinearProgressIndicator(progress.value)
        }
    }
}