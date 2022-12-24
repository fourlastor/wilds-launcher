package io.github.fourlastor.wilds_launcher.app

import net.harawata.appdirs.AppDirs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Dirs @Inject constructor(
    private val appInfo: AppInfo,
    private val appDirs: AppDirs,
) {
    val config: String
        get() = appDirs.getUserConfigDir(appInfo.name, appInfo.version, appInfo.author)

    val data: String
        get() = appDirs.getUserDataDir(appInfo.name, appInfo.version, appInfo.author)
}
