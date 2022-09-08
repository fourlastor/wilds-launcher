package io.github.fourlastor.wilds_launcher.settings

import net.harawata.appdirs.AppDirs

class Dirs(
    private val appDirs: AppDirs,
) {
    val config: String
        get() = appDirs.getUserConfigDir(
            "wilds-launcher",
            "1.0.0",
            "io.github.fourlastor"
        )
}
