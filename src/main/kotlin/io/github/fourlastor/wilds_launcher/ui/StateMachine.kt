package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.fourlastor.wilds_launcher.Context
import io.github.fourlastor.wilds_launcher.jar_picker.JarPicker
import io.github.fourlastor.wilds_launcher.states.DownloaderState
import io.github.fourlastor.wilds_launcher.states.InitialState
import io.github.fourlastor.wilds_launcher.states.JarPickerState
import io.github.fourlastor.wilds_launcher.states.LauncherState
import io.github.fourlastor.wilds_launcher.states.OkDialogState
import io.github.fourlastor.wilds_launcher.states.State
import io.github.fourlastor.wilds_launcher.states.UpdateCheckerState
import io.github.fourlastor.wilds_launcher.states.YesNoDialogState
import java.io.File

@Composable
@Preview
fun StateMachine(context: Context) {
    var state: State by remember { mutableStateOf(InitialState()) }

    if (state is InitialState) {
        val jar = context.settingsService.getJar()

        if (jar.isNotEmpty()) {
            if (File(jar).exists()) {
                state = LauncherState()
            } else {
                state = OkDialogState(listOf("The configured pokewilds.jar does not exist.")) { state = JarPickerState() }
            }
        }
        else {
            state = JarPickerState()
        }
    }

    var devMode by remember { mutableStateOf(context.settingsService.getDevMode()) }
    var logsEnabled by remember { mutableStateOf(context.settingsService.getLogsEnabled()) }
    var angleGles20 by remember { mutableStateOf(context.settingsService.getAngleGles20()) }

    when (state) {
        is OkDialogState -> {
            val okDialogState = state as OkDialogState
            OkDialog(okDialogState.lines, okDialogState.onOk)
        }

        is YesNoDialogState -> {
            val yesNoDialogState = state as YesNoDialogState
            YesNoDialog(
                lines = yesNoDialogState.lines,
                onYes = yesNoDialogState.onYes,
                onNo = yesNoDialogState.onNo
            )
        }

        is JarPickerState -> {
            JarPicker(
                downloadLatestRelease = { state = DownloaderState(
                    onSuccess = {
                        state = OkDialogState(listOf("Successfully downloaded latest release!.")) { state = LauncherState() }
                    },
                    onError = {
                        state = OkDialogState(listOf("Failed to download latest release.")) { state = JarPickerState() }
                    }
                ) },
                findJar = {
                    val jarFile = context.releaseService.findInstallation()

                    if (jarFile == null) {
                        state = OkDialogState(listOf("Could not find pokewilds.jar.")) { state = JarPickerState() }
                        return@JarPicker
                    }

                    context.settingsService.setJar(jarFile.absolutePath)

                    state = OkDialogState(listOf("Found pokewilds.jar!", "(${jarFile.absoluteFile})")) { state = LauncherState() }
                },
                pickJar = { }
            )
        }

        is LauncherState -> {
            Launcher(
                context = context,
                devMode = devMode,
                onDevModeChanged = {
                    context.settingsService.setDevMode(it)
                    devMode = it
                },
                logsEnabled = logsEnabled,
                onLogsEnabledChanged = {
                    context.settingsService.setLogsEnabled(it)
                    logsEnabled = it
                },
                angleGles20 = angleGles20,
                onAngleGles20Changed = {
                    context.settingsService.setAngleGles20(it)
                    angleGles20 = it
                },
                clearSettings = {
                    context.settingsService.clear()
                    state = JarPickerState()
                },
                checkForUpdates = { state = UpdateCheckerState() }
            )
        }

        is UpdateCheckerState -> {
            UpdateChecker(
                context = context,
                onUpdateFound = {
                    state = YesNoDialogState(
                        lines = listOf("There is an update available (${context.releaseService.getLatestReleaseVersion()}).", "Do you want to download it?"),
                        onYes = { state = DownloaderState(
                            onSuccess = {
                                state = OkDialogState(listOf("Successfully downloaded latest release!")) { state = LauncherState() }
                            },
                            onError = {
                                state = OkDialogState(listOf("Failed to download latest release.")) { state = LauncherState() }
                            }
                        ) },
                        onNo = { state = LauncherState() }
                    )
                },
                onNoUpdateFound = {
                    state = OkDialogState(
                        listOf("No updates found.")
                    ) { state = LauncherState() }
                },
                onError = {
                    state = OkDialogState(
                        listOf("Failed to check for updates.")
                    ) { state = LauncherState()
                }
            })
        }

        is DownloaderState -> {
            val downloaderState = state as DownloaderState
            Downloader(context, downloaderState.onSuccess, downloaderState.onError)
        }
    }
}
