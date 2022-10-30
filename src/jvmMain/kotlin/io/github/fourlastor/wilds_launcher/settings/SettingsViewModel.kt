package io.github.fourlastor.wilds_launcher.settings

import io.github.fourlastor.wilds_launcher.state.Manager
import io.github.fourlastor.wilds_launcher.state.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel constructor(
    val repository: SettingsRepository,
    dispatchers: Dispatchers,
) : ViewModel<SettingsState> {
    val scope = CoroutineScope(dispatchers.Default)
    val manager = Manager<SettingsState>(SettingsState.Loading)

    override fun start() {
        scope.launch {
            val data = repository.read()

            if (data != null) {
                val jarFile = File(File(data.dir), data.jar)

                if (jarFile.exists()) {
                    manager.update {
                        SettingsState.Loaded(
                            dir = data.dir,
                            jar = data.jar,
                            devMode = data.devMode,
                            logsEnabled = data.logsEnabled,
                            angleGles20 = data.angleGles20,
                            logs = ""
                        )
                    }
                }
                else {
                    manager.update { SettingsState.Missing }
                }
            }
            else {
                manager.update { SettingsState.Missing }
            }

            manager.state
                .drop(1)
                .mapNotNull { it as? SettingsState.Loaded }
                .map {
                    SettingsData(
                        dir = it.dir,
                        jar = it.jar,
                        devMode = it.devMode,
                        logsEnabled = it.logsEnabled,
                        angleGles20 = it.angleGles20
                    )
                }
                .collect { repository.save(it) }
        }
    }

    override fun stop() {
        scope.cancel()
    }

    override val state = manager.state
}
