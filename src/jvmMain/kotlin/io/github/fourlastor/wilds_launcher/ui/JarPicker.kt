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

const val FILENAME = "pokewilds.jar"
const val FILENAME_ALT = "pokemon-wilds.jar"

@Composable
fun JarPicker(
    downloadLatestRelease: () -> Unit,
    findJar: () -> Unit,
    pickJar: () -> Pair<String, String>?,
    saveWildsDir: (String, String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(downloadLatestRelease) {
                Text("Download latest release")
            }

            Text("or")

            Button(findJar, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) {
                Text("Find pokewilds.jar")
            }

            Text("or")

            Button({
                pickJar()
                    ?.takeIf { (_, jar) -> jar == FILENAME || jar == FILENAME_ALT }
                    ?.also { (dir, jar) -> saveWildsDir(dir, jar) }
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) {
                Text("Select pokewilds.jar")
            }
        }
    }
}
