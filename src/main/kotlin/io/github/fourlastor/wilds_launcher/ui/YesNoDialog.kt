package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun YesNoDialog(lines: List<String>, onYes: () -> Unit, onNo: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            lines.forEach { Text(it) }

            Row {
                Button(onYes) { Text("Yes") }
                Spacer(Modifier.width(Dp(10f)))
                Button(onNo, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) { Text("No") }
            }
        }
    }
}
