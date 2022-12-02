package io.github.fourlastor.wilds_launcher.app

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppInfoModule {
    @Singleton
    @Provides
    fun appInfo() = AppInfo(
        title = "Pokewilds Launcher",
        name = "wilds-launcher",
        version = "1.0.0",
        author = "io.github.fourlastor",
    )
}
