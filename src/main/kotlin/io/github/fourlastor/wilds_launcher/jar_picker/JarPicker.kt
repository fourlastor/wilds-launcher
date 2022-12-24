package io.github.fourlastor.wilds_launcher.jar_picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun JarPicker(
    downloadLatestRelease: () -> Unit,
    findJar: () -> Unit,
    pickJar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
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

            Button(pickJar, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) {
                Text("Select pokewilds.jar")
            }
        }
    }
}
