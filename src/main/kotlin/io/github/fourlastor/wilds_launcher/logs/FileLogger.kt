package io.github.fourlastor.wilds_launcher.logs

import java.io.File
import java.lang.StringBuilder

class FileLogger(
    private val file: File
) : Logger {
    private var logs: StringBuilder = StringBuilder()

    override fun log(message: String) {
        logs.appendLine(message)
        println(message)
    }

    override fun shutdown() {
        if (logs.isBlank()) {
            return
        }

        file.writeText(logs.toString())
    }
}