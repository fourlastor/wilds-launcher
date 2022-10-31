package io.github.fourlastor.wilds_launcher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import io.github.fourlastor.wilds_launcher.getLatestReleaseVersion
import io.github.fourlastor.wilds_launcher.runPokeWilds
import io.github.fourlastor.wilds_launcher.settings.Settings
import io.github.fourlastor.wilds_launcher.states.*
import io.github.fourlastor.wilds_launcher.states.State
import io.github.fourlastor.wilds_launcher.ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun StateMachine(
    settings: Settings,
    scope: CoroutineScope,
    getPokeWildsLocation: () -> Pair<String, String>?,
    devMode: Boolean,
    onDevModeChanged: (Boolean) -> Unit,
    saveWildsDir: (String, String) -> Unit,
    clearData: () -> Unit,
    log: (String) -> Unit,
) {
    var devMode by remember { mutableStateOf(devMode) }

    var state: State by remember { mutableStateOf(InitialState()) }

    if (state is InitialState) {
        state = if (settings.dir.isNotEmpty() && settings.jar.isNotEmpty() && File(settings.dir, settings.jar).exists()) LauncherState() else JarPickerState()
    }

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
                pickFile = getPokeWildsLocation,
                saveWildsDir = { dir, jar ->
                    saveWildsDir(dir, jar)
                    state = LauncherState()
                }
            )
        }

        is LauncherState -> {
            Launcher(
                scope = scope,
                directory = File(settings.dir),
                devMode = devMode,
                onDevModeChanged = {
                    devMode = it
                    onDevModeChanged(it)
                },
                logsEnabled = settings.logsEnabled,
                onLogsEnabledChanged = { settings.logsEnabled = it },
                angleGles20 = settings.angleGles20,
                onAngleGles20Changed = { settings.angleGles20 = it },
                runPokeWilds = {
                    val context = newSingleThreadContext("pokeWildsJar")

                    scope.launch(context) {
                        var log: ((String) -> Unit)? = null

                        if (settings.logsEnabled) {
                            log = { log(it) }
                        }

                        runPokeWilds(
                            File(settings.dir, settings.jar),
                            settings.angleGles20,
                            settings.devMode,
                            log
                        )
                    }
                },
                clearData = {
                    clearData()
                    state = JarPickerState()
                },
                checkForUpdates = { state = UpdateCheckerState() }
            )
        }

        is UpdateCheckerState -> {
            UpdateChecker(
                settings = settings,
                scope = scope,
                onUpdateFound = {
                    state = YesNoDialogState(
                        lines = arrayOf("There is an update available (${getLatestReleaseVersion()}).", "Do you want to download it?"),
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
            Downloader(scope, { dir, jar ->
                settings.dir = dir
                settings.jar = jar

                state = LauncherState()
            }, downloaderState.onError)
        }
    }
}
