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
                viewModel,
                settingsState = settingsState,
                onDevModeChanged = {
                    viewModel.devMode(it)
                },
                onLogsEnabledChanged = {
                    viewModel.logsEnabled(it)
                },
                onAngleGles20Changed = {
                    viewModel.angleGles20(it)
                },
                runPokeWilds = { state ->
                    val context = newSingleThreadContext("pokeWildsJar")

                    viewModel.scope.launch(context) {
                        var log: ((String) -> Unit)? = null

                        if (state.logsEnabled) {
                            log = { state.appendLog(it) }
                        }

                        runPokeWilds(File(state.dir, state.jar), state.angleGles20, state.devMode, log)
                    }
                },
                clearData = {
                    viewModel.clearData()
                }
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
