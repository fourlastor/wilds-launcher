package io.github.fourlastor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.fourlastor.settings.SettingsViewModel

@Composable
fun JarPicker(
    pickFile: () -> Pair<String, String>?,
    viewModel: SettingsViewModel
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button({
            pickFile()?.also { (dir, jar) ->
                viewModel.saveWildsDir(dir, jar)
            }
        }) {
            Text("Find pokewilds.jar file")
        }
    }
}
