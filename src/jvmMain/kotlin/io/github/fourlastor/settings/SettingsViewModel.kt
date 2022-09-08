package io.github.fourlastor.settings

import io.github.fourlastor.state.Manager
import io.github.fourlastor.state.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

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
            } else {
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

    fun saveWildsDir(dir: String, jar: String) {
        manager.update { it.wildsDir(dir, jar) }
    }

    fun devMode(devMode: Boolean) {
        manager.update { it.devMode(devMode) }
    }

    fun logsEnabled(logsEnabled: Boolean) {
        manager.update { it.logsEnabled(logsEnabled) }
    }

    fun angleGles20(angleGles20: Boolean) {
        manager.update { it.angleGles20(angleGles20) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun runPokeWilds(state: SettingsState.Loaded) {
        val jarFile = File(File(state.dir), state.jar)
        val runArgs = mutableListOf("java", "-jar", jarFile.absolutePath).apply {
            if (state.angleGles20) {
                add("angle_gles20")
            }
            if (state.devMode) {
                add("dev")
            }
        }.toTypedArray()

        scope.launch(newSingleThreadContext("pokeWildsJar")) {
            try {
                if (state.logsEnabled) {
                    appendLog("Running ${runArgs.joinToString(" ")}")
                }
                val proc = ProcessBuilder(*runArgs)
                    .directory(File(state.dir))
                    .start()
                captureLogs(state, proc)
            } catch (exception: Throwable) {
                if (state.logsEnabled) {
                    appendLog(exception.fullTrace())
                }
            }

        }
    }

    private fun Throwable.fullTrace(): String = """
        $message
        ${stackTraceToString()}
        ${cause?.also { "Caused by: ${it.fullTrace()}" }}
    """.trimIndent()

    private fun captureLogs(state: SettingsState.Loaded, proc: Process) {
        if (state.logsEnabled) {
            val stdInput = BufferedReader(InputStreamReader(proc.inputStream))

            val stdError = BufferedReader(InputStreamReader(proc.errorStream))

            var s = ""
            while (stdInput.readLine()?.also { s = it } != null) {
                appendLog(s)
            }

            while (stdError.readLine()?.also { s = it } != null) {
                appendLog(s)
            }
        }
    }

    private fun appendLog(log: String) {
        println(log)
        manager.update { it.appendLog(log) }
    }
}
