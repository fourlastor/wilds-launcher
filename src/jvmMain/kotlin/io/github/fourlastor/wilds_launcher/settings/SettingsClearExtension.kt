package io.github.fourlastor.wilds_launcher.settings

fun Settings.clear() {
    this.dir = ""
    this.jar = ""
    this.devMode = false
    this.logsEnabled = false
    this.angleGles20 = false
}