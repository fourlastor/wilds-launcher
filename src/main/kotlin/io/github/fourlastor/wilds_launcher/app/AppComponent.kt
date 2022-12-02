package io.github.fourlastor.wilds_launcher.app

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppInfoModule::class])
interface AppComponent {

    fun app(): LauncherApp
}
