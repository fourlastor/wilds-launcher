package io.github.fourlastor.wilds_launcher

import io.github.fourlastor.wilds_launcher.settings.SettingsState
import io.github.fourlastor.wilds_launcher.settings.SettingsViewModel
import kotlinx.coroutines.*
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
fun runPokeWilds(viewModel : SettingsViewModel, state: SettingsState.Loaded, log: ((String) -> Unit)? = null) {
    val jarFile = File(File(state.dir), state.jar)
    val runArgs = mutableListOf("java", "-jar", jarFile.absolutePath).apply {
        if (state.angleGles20) {
            add("angle_gles20")
        }
        if (state.devMode) {
            add("dev")
        }
    }.toTypedArray()

    viewModel.scope.launch(newSingleThreadContext("pokeWildsJar")) {
        try {
            log?.invoke("Running ${runArgs.joinToString(" ")}")

            val proc = withContext(Dispatchers.IO) {
                ProcessBuilder(*runArgs)
                    .directory(File(state.dir))
                    .start()
            }

            if (log != null) {
                proc.inputStream.capture(log)
                proc.errorStream.capture(log)
            }
        } catch (exception: Throwable) {
            log?.invoke(exception.fullTrace())
        }
    }
}
