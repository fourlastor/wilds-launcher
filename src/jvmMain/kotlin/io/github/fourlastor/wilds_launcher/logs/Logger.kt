package io.github.fourlastor.wilds_launcher.logs

interface Logger {
    fun log(message: String)
    fun shutdown()
}