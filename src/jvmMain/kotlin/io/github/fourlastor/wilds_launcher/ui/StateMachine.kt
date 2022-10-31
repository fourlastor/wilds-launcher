package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import io.github.fourlastor.wilds_launcher.Context
import io.github.fourlastor.wilds_launcher.states.*
import io.github.fourlastor.wilds_launcher.states.State
import java.io.File
import java.nio.file.Paths

@Composable
@Preview
fun StateMachine(context: Context, getPokeWildsLocation: () -> Pair<String, String>?) {
    var state: State by remember { mutableStateOf(InitialState()) }

    if (state is InitialState) {
        val dir = context.settingsService.getDir()
        val jar = context.settingsService.getJar()

        state = if (dir.isNotEmpty() && jar.isNotEmpty() && File(dir, jar).exists()) LauncherState() else JarPickerState()
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
                downloadLatestRelease = { state = DownloaderState {
                    state = OkDialogState(arrayOf("Failed to download latest release.")) { state = JarPickerState() }
                } },
                findJar = {
                    val workingDirectory = File(Paths.get("").toAbsolutePath().toString())
                    val jarFile = workingDirectory.walkTopDown()
                        .filter { it.isFile }
                        .filter { it.name == FILENAME || it.name == FILENAME_ALT }
                        .firstOrNull()

                    if (jarFile == null) {
                        state = OkDialogState(arrayOf("Could not find pokewilds.jar.")) { state = JarPickerState() }
                        return@JarPicker
                    }

                    val directory = jarFile.parentFile.absolutePath
                    val jar = jarFile.name

                    context.settingsService.setDir(directory)
                    context.settingsService.setJar(jar)

                    state = OkDialogState(arrayOf("Found pokewilds.jar!", "(${jarFile.absoluteFile})")) { state = LauncherState() }
                },
                pickJar = getPokeWildsLocation,
                saveWildsDir = { dir, jar ->
                    context.settingsService.setDir(dir)
                    context.settingsService.setJar(jar)

                    state = LauncherState()
                }
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
                        lines = arrayOf("There is an update available (${context.releaseService.getLatestReleaseVersion()}).", "Do you want to download it?"),
                        onYes = { state = DownloaderState {
                            state = OkDialogState(arrayOf("Failed to download latest release.")) { state = LauncherState() }
                        } },
                        onNo = { state = LauncherState() }
                    )
                },
                onNoUpdateFound = {
                    state = OkDialogState(
                        arrayOf("No updates found.")
                    ) { state = LauncherState() }
                },
                onError = {
                    state = OkDialogState(
                        arrayOf("Failed to check for updates.")
                    ) { state = LauncherState()
                }
            })
        }

        is DownloaderState -> {
            val downloaderState = state as DownloaderState
            Downloader(context, { dir, jar ->
                context.settingsService.setDir(dir)
                context.settingsService.setJar(jar)

                state = LauncherState()
            }, downloaderState.onError)
        }
    }
}
