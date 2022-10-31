package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.fourlastor.wilds_launcher.getInstalledReleaseVersion
import io.github.fourlastor.wilds_launcher.getLatestReleaseVersion
import io.github.fourlastor.wilds_launcher.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun UpdateChecker(settings: Settings, scope: CoroutineScope, onUpdateFound: () -> Unit, onNoUpdateFound: () -> Unit, onError: () -> Unit) {
    scope.launch {
        val installedReleaseVersion = getInstalledReleaseVersion(File(settings.dir))
        val latestReleaseVersion = getLatestReleaseVersion()

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