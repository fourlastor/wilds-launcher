package io.github.fourlastor.wilds_launcher.app

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dagger.Module
import dagger.Provides
import net.harawata.appdirs.AppDirs
import net.harawata.appdirs.AppDirsFactory
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun appInfo() = AppInfo(
        title = "Pokewilds Launcher",
        name = "wilds-launcher",
        version = "1.0.0",
        author = "io.github.fourlastor",
    )

    @Provides
    @Singleton
    fun appDirs(): AppDirs = AppDirsFactory.getInstance()

    @Singleton
    @Provides
    fun lifecycleRegistry() = LifecycleRegistry()
}
