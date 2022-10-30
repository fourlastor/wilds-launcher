package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.fourlastor.wilds_launcher.getInstalledReleaseVersion
import io.github.fourlastor.wilds_launcher.getLatestReleaseChangelog
import java.io.File

@Composable
fun Launcher(
    directory: File,
    devMode: Boolean,
    onDevModeChanged: (Boolean) -> Unit,
    logsEnabled: Boolean,
    onLogsEnabledChanged: (Boolean) -> Unit,
    angleGles20: Boolean,
    onAngleGles20Changed: (Boolean) -> Unit,
    runPokeWilds: () -> Unit,
    clearData: () -> Unit,
    checkForUpdates: () -> Unit
) {
    val scrollState = rememberScrollState()
    val changelog = remember { mutableStateOf("Loading changelog...") }

    changelog.value = getLatestReleaseChangelog() ?: "Failed to load changelog."

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier.fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        Text(changelog.value)
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Installed: ${getInstalledReleaseVersion(directory)}")
        }

        Column {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Row {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = devMode,
                            onCheckedChange = onDevModeChanged,
                        )
                        Text("Dev Mode")
                    }

                    Spacer(modifier = Modifier.width(Dp(10f)))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = logsEnabled,
                            onCheckedChange = onLogsEnabledChanged,
                        )
                        Text("Logs")
                    }

                    Spacer(modifier = Modifier.width(Dp(10f)))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = angleGles20,
                            onCheckedChange = onAngleGles20Changed,
                        )
                        Text("Compatibility Mode")
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button({ clearData() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray, contentColor = Color.Red)) {
                    Text(text = "Clear Launcher Data")
                }

                Spacer(modifier = Modifier.width(Dp(10f)))

                Button(checkForUpdates) {
                    Text(text = "Check for Updates")
                }

                Spacer(modifier = Modifier.width(Dp(10f)))

                Button(runPokeWilds) {
                    Text("Start PokeWilds")
                }
            }
        }
    }
}