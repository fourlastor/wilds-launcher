package io.github.fourlastor.wilds_launcher.app

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun app(): LauncherApp
}
