package io.github.fourlastor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fourlastor.settings.SettingsState

@Composable
fun Launcher(
    settingsState: SettingsState.Loaded,
    onDevModeChanged: (Boolean) -> Unit,
    onAngleGles20Changed: (Boolean) -> Unit,
    runPokeWilds: (SettingsState.Loaded) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = settingsState.devMode,
                onCheckedChange = onDevModeChanged,
            )
            Text("Dev mode")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = settingsState.angleGles20,
                onCheckedChange = onAngleGles20Changed,
            )
            Text("Compatibility mode")
        }

        Button({ runPokeWilds(settingsState) }) {
            Text("Start PokeWilds")
        }
    }
}
