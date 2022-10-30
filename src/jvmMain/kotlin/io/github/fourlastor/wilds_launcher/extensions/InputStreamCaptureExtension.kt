package io.github.fourlastor.wilds_launcher.extensions

import java.io.InputStream
import java.io.InputStreamReader

fun InputStream.capture(log: (String) -> Unit) {
    val reader = InputStreamReader(this).buffered()
    var line = ""

    while (reader.readLine()?.also { line = it } != null) {
        log(line)
    }
}