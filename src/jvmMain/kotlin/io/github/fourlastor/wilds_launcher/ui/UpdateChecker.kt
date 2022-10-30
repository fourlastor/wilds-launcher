package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.fourlastor.wilds_launcher.getLatestReleaseVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun UpdateChecker(scope: CoroutineScope, onUpdateFound: () -> Unit) {
    scope.launch {
        //val installedReleaseVersion = getInstalledReleaseVersion(viewModel.manager.state.value as SettingsState.Loaded)
        val latestReleaseVersion = getLatestReleaseVersion() ?: return@launch

        //if (installedReleaseVersion != null && installedReleaseVersion >= latestReleaseVersion) {
            onUpdateFound()
        //}

        //viewModel.manager.update { SettingsState.Downloading }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Checking for updates...")
    }
}