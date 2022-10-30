package io.github.fourlastor.wilds_launcher

import io.github.fourlastor.wilds_launcher.extensions.capture
import io.github.fourlastor.wilds_launcher.extensions.fullTrace
import java.io.File

fun runPokeWilds(file: File, angleGles20: Boolean, devMode: Boolean, log: ((String) -> Unit)? = null) {
    val runArgs = mutableListOf("java", "-jar", file.absolutePath).apply {
        if (angleGles20) {
            add("angle_gles20")
        }

        if (devMode) {
            add("dev")
        }
    }.toTypedArray()

    try {
        log?.invoke("Running ${runArgs.joinToString(" ")}")

        val process = ProcessBuilder(*runArgs).directory(file.parentFile).start()

        if (log != null) {
            process.inputStream.capture(log)
            process.errorStream.capture(log)
        }
    } catch (exception: Throwable) {
        log?.invoke(exception.fullTrace())
    }
}
