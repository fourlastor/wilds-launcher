package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.fourlastor.wilds_launcher.getInstalledReleaseVersion
import io.github.fourlastor.wilds_launcher.getLatestReleaseVersion
import io.github.fourlastor.wilds_launcher.settings.SettingsState
import io.github.fourlastor.wilds_launcher.settings.SettingsViewModel

@Composable
fun Launcher(
    viewModel: SettingsViewModel,
    settingsState: SettingsState.Loaded,
    onDevModeChanged: (Boolean) -> Unit,
    onLogsEnabledChanged: (Boolean) -> Unit,
    onAngleGles20Changed: (Boolean) -> Unit,
    runPokeWilds: (SettingsState.Loaded) -> Unit,
    clearData: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        Column {
            Text(settingsState.logs)

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Row {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = settingsState.devMode,
                            onCheckedChange = onDevModeChanged,
                        )
                        Text("Dev mode")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = settingsState.logsEnabled,
                            onCheckedChange = onLogsEnabledChanged,
                        )
                        Text("Enable logs")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = settingsState.angleGles20,
                            onCheckedChange = onAngleGles20Changed,
                        )
                        Text("Compatibility mode")
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button({ clearData() }) {
                    Text(text = "Clear launcher data", color = Color.Red)
                }

                Spacer(modifier = Modifier.width(Dp(10f)))

                Button({
                    val installedReleaseVersion = getInstalledReleaseVersion(settingsState)
                    val latestReleaseVersion = getLatestReleaseVersion() ?: return@Button

                    if (installedReleaseVersion != null && installedReleaseVersion >= latestReleaseVersion) {
                        return@Button
                    }

                    viewModel.manager.update { SettingsState.Downloading }
                }) {
                    Text(text = "Check for updates")
                }

                Spacer(modifier = Modifier.width(Dp(10f)))

                Button({ runPokeWilds(settingsState) }) {
                    Text("Start PokeWilds")
                }
            }
        }
    }
}