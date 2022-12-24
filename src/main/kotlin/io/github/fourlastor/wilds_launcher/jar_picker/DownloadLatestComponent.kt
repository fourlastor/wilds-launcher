package io.github.fourlastor.wilds_launcher.jar_picker

import androidx.compose.foundation.layout.Column
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import io.github.fourlastor.wilds_launcher.extension.createScope
import io.github.fourlastor.wilds_launcher.navigation.Component
import io.github.fourlastor.wilds_launcher.releases.services.GitHubReleaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadLatestComponent(
    context: ComponentContext,
    private val releaseService: GitHubReleaseService,
    private val onDownload: (File) -> Unit,
) : Component, ComponentContext by context {

    private val scope = createScope()
    private val progress = MutableStateFlow(0f)

    init {
        scope.launch {
            val file = withContext(Dispatchers.IO) {
                releaseService.downloadLatestRelease { amount -> progress.update { amount } }
            } ?: return@launch
            withContext(Dispatchers.Main) {
                onDownload(file)
            }
        }
    }

    @Composable
    override fun render() {
        val amount by progress.collectAsState()
        Column {
            Text("Downloading...")
            LinearProgressIndicator(amount)
        }
    }
}
