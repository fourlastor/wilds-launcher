package io.github.fourlastor.settings

import io.github.fourlastor.state.Manager
import io.github.fourlastor.state.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class SettingsViewModel constructor(
    private val repository: SettingsRepository,
    dispatchers: Dispatchers,
) : ViewModel<SettingsState> {
    private val scope = CoroutineScope(dispatchers.Default)
    private val manager = Manager<SettingsState>(SettingsState.Loading)

    override fun start() {
        scope.launch {
            val data = repository.read()
            if (data != null) {
                manager.update { SettingsState.Loaded(data.dir, data.jar) }
            } else {
                manager.update { SettingsState.Missing }
            }

            manager.state
                .drop(1)
                .mapNotNull { it as? SettingsState.Loaded }
                .map { SettingsData(it.dir, it.jar) }
                .collect { repository.save(it) }
        }
    }

    override fun stop() {
        scope.cancel()
    }

    override val state = manager.state

    fun saveWildsDir(dir: String, jar: String) {
        manager.update { it.wildsDir(dir, jar) }
    }
}
