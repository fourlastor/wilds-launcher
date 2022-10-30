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

                        runPokeWilds(File(settingsState.dir, settingsState.jar), settingsState.angleGles20, settingsState.devMode, log)
                    }
                },
                clearData = { viewModel.clearData() },
                checkForUpdates = { viewModel.manager.update { SettingsState.CheckingForUpdates } }
            )

            is SettingsState.Downloading -> Downloader(viewModel)

            is SettingsState.CheckingForUpdates -> UpdateChecker(viewModel)
            is SettingsState.NoUpdatesFound -> OkDialog(arrayOf("No available update found.")) { viewModel.manager.update { SettingsState.Missing } }

            is SettingsState.UpdateFound -> YesNoDialog(
                arrayOf("There is an update available (${getLatestReleaseVersion()}).", "Do you want to download it?"),
                { viewModel.manager.update { SettingsState.Downloading } },
                { viewModel.manager.update { SettingsState.Missing } }
            )

            is SettingsState.Missing -> JarPicker(getPokeWildsLocation, viewModel)
            is SettingsState.Loading -> {}
        }
    }
}

class AppComponent : KoinComponent {
    val settingsViewModel by inject<SettingsViewModel>()
}
