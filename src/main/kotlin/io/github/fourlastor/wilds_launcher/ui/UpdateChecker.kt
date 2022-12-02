package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.fourlastor.wilds_launcher.Context
import io.github.fourlastor.wilds_launcher.releases.getInstalledReleaseSizeInBytes
import io.github.fourlastor.wilds_launcher.releases.getInstalledReleaseVersion
import kotlinx.coroutines.launch

@Composable
fun UpdateChecker(context: Context, onUpdateFound: () -> Unit, onNoUpdateFound: () -> Unit, onError: () -> Unit) {
    context.coroutineScope.launch {
        val latestReleaseVersion = context.releaseService.getLatestReleaseVersion()

        if (latestReleaseVersion == null) {
            onError()
            return@launch
        }

        val installedReleaseVersion = context.getInstalledReleaseVersion()

        if (installedReleaseVersion != null && installedReleaseVersion >= latestReleaseVersion) {
            val installedReleaseSizeInBytes = context.getInstalledReleaseSizeInBytes()

            if (installedReleaseSizeInBytes != null) {
                val latestReleaseSizeInBytes = context.releaseService.getLatestReleaseSizeInBytes()

                if (latestReleaseSizeInBytes == null) {
                    onError()
                    return@launch
                }

                // We might have mods installed, which increases our installed release file size.
                if (installedReleaseSizeInBytes >= latestReleaseSizeInBytes) {
                    onNoUpdateFound()
                    return@launch
                }
            }
        }

        onUpdateFound()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Checking for updates...")
    }
}