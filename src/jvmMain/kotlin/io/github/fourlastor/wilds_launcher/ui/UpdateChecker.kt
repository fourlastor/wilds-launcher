package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.fourlastor.wilds_launcher.Context
import io.github.fourlastor.wilds_launcher.releases.getInstalledReleaseVersion
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun UpdateChecker(context: Context, onUpdateFound: () -> Unit, onNoUpdateFound: () -> Unit, onError: () -> Unit) {
    context.coroutineScope.launch {
        val installedReleaseVersion = getInstalledReleaseVersion(File(context.settingsService.getDir()))
        val latestReleaseVersion = context.releaseService.getLatestReleaseVersion()

        if (latestReleaseVersion == null) {
            onError()
            return@launch
        }

        if (installedReleaseVersion != null && installedReleaseVersion >= latestReleaseVersion) {
            onNoUpdateFound()
            return@launch
        }

        onUpdateFound()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Checking for updates...")
    }
}