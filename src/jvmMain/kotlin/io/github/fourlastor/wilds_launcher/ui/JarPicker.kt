package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.fourlastor.wilds_launcher.downloadLatestRelease
import io.github.fourlastor.wilds_launcher.settings.SettingsViewModel
import java.io.File

const val FILENAME = "pokewilds.jar"
const val FILENAME_ALT = "pokemon-wilds.jar"

@Composable
fun JarPicker(
    pickFile: () -> Pair<String, String>?,
    viewModel: SettingsViewModel
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button({
                val rootDirectory = downloadLatestRelease() ?: return@Button
                val gameDirectory = rootDirectory.walk().drop(1).first()
                val appDirectory = gameDirectory.absolutePath + File.separator + "app"

                viewModel.saveWildsDir(appDirectory, FILENAME)
            }) {
                Text("Download latest release")
            }
            Text("or")
            Button({
                pickFile()
                    ?.takeIf { (_, jar) -> jar == FILENAME || jar == FILENAME_ALT }
                    ?.also { (dir, jar) ->
                        viewModel.saveWildsDir(dir, jar)
                    }
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) {
                Text("Select pokewilds.jar file")
            }
        }
    }
}
