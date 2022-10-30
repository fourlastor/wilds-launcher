package io.github.fourlastor.wilds_launcher

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import io.github.fourlastor.wilds_launcher.settings.SettingsState
import io.github.fourlastor.wilds_launcher.settings.SettingsViewModel
import io.github.fourlastor.wilds_launcher.state.ViewModelContainer
import io.github.fourlastor.wilds_launcher.ui.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun App(appComponent: AppComponent, getPokeWildsLocation: () -> Pair<String, String>?) {
    val viewModel = appComponent.settingsViewModel

    ViewModelContainer(viewModel) { settingsState ->
        when (settingsState) {
            is SettingsState.Loaded -> Launcher(
                scope = viewModel.scope,
                directory = File(settingsState.dir),
                devMode = settingsState.devMode,
                onDevModeChanged = { devMode -> viewModel.manager.update { it.devMode(devMode) } },
                logsEnabled = settingsState.logsEnabled,
                onLogsEnabledChanged = { logsEnabled -> viewModel.manager.update { it.logsEnabled(logsEnabled) } },
                angleGles20 = settingsState.angleGles20,
                onAngleGles20Changed = { angleGles20 -> viewModel.manager.update { it.angleGles20(angleGles20) } },
                runPokeWilds = {
                    val context = newSingleThreadContext("pokeWildsJar")

                    viewModel.scope.launch(context) {
                        var log: ((String) -> Unit)? = null

                        if (settingsState.logsEnabled) {
                            log = { settingsState.appendLog(it) }
                        }

                        runPokeWilds(
                            File(settingsState.dir, settingsState.jar),
                            settingsState.angleGles20,
                            settingsState.devMode,
                            log
                        )
                    }
                },
                clearData = {
                    viewModel.scope.launch {
                        viewModel.repository.clear()
                        viewModel.manager.update { SettingsState.Missing }
                    }
                },
                checkForUpdates = { viewModel.manager.update { SettingsState.CheckingForUpdates } }
            )

            is SettingsState.Downloading -> Downloader(viewModel.scope) { dir, jar ->
                viewModel.manager.update { it.wildsDir(dir, jar) }
            }

            is SettingsState.CheckingForUpdates -> UpdateChecker(viewModel.scope) {
                viewModel.manager.update { SettingsState.UpdateFound }
            }

            is SettingsState.NoUpdatesFound -> OkDialog(arrayOf("No available update found.")) {
                viewModel.manager.update { SettingsState.Missing }
            }

            is SettingsState.UpdateFound -> YesNoDialog(
                lines = arrayOf("There is an update available (${getLatestReleaseVersion()}).", "Do you want to download it?"),
                onYes = { viewModel.manager.update { SettingsState.Downloading } },
                onNo = { viewModel.manager.update { SettingsState.Missing } }
            )

            is SettingsState.Missing -> JarPicker(
                downloadLatestRelease = { viewModel.manager.update { SettingsState.Downloading } },
                pickFile = getPokeWildsLocation,
                saveWildsDir = { dir, jar ->viewModel.manager.update { it.wildsDir(dir, jar) } }
            )

            is SettingsState.Loading -> {}
        }
    }
}

class AppComponent : KoinComponent {
    val settingsViewModel by inject<SettingsViewModel>()
}
