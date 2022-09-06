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
import java.io.File

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
                manager.update { SettingsState.Loaded(
                    dir = data.dir,
                    jar = data.jar,
                    devMode = data.devMode,
                    angleGles20 = data.angleGles20
                ) }
            } else {
                manager.update { SettingsState.Missing }
            }

            manager.state
                .drop(1)
                .mapNotNull { it as? SettingsState.Loaded }
                .map { SettingsData(
                    dir = it.dir,
                    jar = it.jar,
                    devMode = it.devMode,
                    angleGles20 = it.angleGles20
                ) }
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

    fun devMode(devMode: Boolean) {
        manager.update { it.devMode(devMode) }
    }

    fun angleGles20(angleGles20: Boolean) {
        manager.update { it.angleGles20(angleGles20) }
    }

    override fun runPokeWilds(state: SettingsState.Loaded) {
        val runArgs = mutableListOf("java", "-jar", state.jar).apply {
            if (state.angleGles20) {
                add("angle_gles20")
            }
            if (state.devMode) {
                add("dev")
            }
        }.toTypedArray()

        scope.launch(Dispatchers.IO) {
            val proc = Runtime.getRuntime().exec(
                runArgs,
                null,
                File(state.dir)
            )
        }
    }
}
