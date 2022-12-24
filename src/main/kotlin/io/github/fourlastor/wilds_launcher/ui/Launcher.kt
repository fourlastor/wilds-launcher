package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.fourlastor.wilds_launcher.Context
import io.github.fourlastor.wilds_launcher.extension.capture
import io.github.fourlastor.wilds_launcher.extension.fullTrace
import io.github.fourlastor.wilds_launcher.releases.getInstalledReleaseVersion
import kotlinx.coroutines.*
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Launcher(
    context: Context,
    devMode: Boolean,
    onDevModeChanged: (Boolean) -> Unit,
    logsEnabled: Boolean,
    onLogsEnabledChanged: (Boolean) -> Unit,
    angleGles20: Boolean,
    onAngleGles20Changed: (Boolean) -> Unit,
    clearSettings: () -> Unit,
    checkForUpdates: () -> Unit
) {
    val scrollState = rememberScrollState()

    var changelog by remember { mutableStateOf("Loading changelog...") }
    var isPokeWildsStarted by remember { mutableStateOf(false) }

    context.coroutineScope.launch {
        changelog = context.releaseService.getLatestReleaseChangelog() ?: "Failed to load changelog."
    }

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier.fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        Text(changelog)
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Installed: ${context.getInstalledReleaseVersion()}")
        }

        Column {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Row {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = devMode,
                            onCheckedChange = onDevModeChanged
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
                Button({ clearSettings() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray, contentColor = Color.Red)) {
                    Text(text = "Clear Settings")
                }

                Spacer(modifier = Modifier.width(Dp(10f)))

                Button(checkForUpdates) {
                    Text(text = "Check for Updates")
                }

                Spacer(modifier = Modifier.width(Dp(10f)))

                if (isPokeWildsStarted) {
                    Button({
                        context.process!!.destroy()
                        context.process = null
                    }, colors = ButtonDefaults.buttonColors(Color.Red)) {
                        Text("Stop PokeWilds")
                    }
                }
                else {
                    Button({
                        val threadContext = newSingleThreadContext("pokeWildsJar")

                        context.coroutineScope.launch(threadContext) {
                            var log: ((String) -> Unit)? = null

                            if (context.settingsService.getLogsEnabled()) {
                                log = { context.logger.log(it) }
                            }

                            val file = File(context.settingsService.getJar())

                            val runArgs = mutableListOf("java", "-jar", file.absolutePath).apply {
                                if (context.settingsService.getAngleGles20()) {
                                    add("angle_gles20")
                                }

                                if (context.settingsService.getDevMode()) {
                                    add("dev")
                                }
                            }.toTypedArray()

                            try {
                                log?.invoke("Running ${runArgs.joinToString(" ")}")

                                val process = withContext(Dispatchers.IO) {
                                    ProcessBuilder(*runArgs).directory(file.parentFile).start()
                                }

                                if (log != null) {
                                    process.inputStream.capture(log)
                                    process.errorStream.capture(log)
                                }

                                context.process = process

                                isPokeWildsStarted = true
                                process.onExit().thenApply { isPokeWildsStarted = false }
                            } catch (exception: Throwable) {
                                log?.invoke(exception.fullTrace())
                            }
                        }
                    }) {
                        Text("Start PokeWilds")
                    }
                }
            }
        }
    }
}
