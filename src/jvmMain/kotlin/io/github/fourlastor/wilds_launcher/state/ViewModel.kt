package io.github.fourlastor.wilds_launcher.state

import io.github.fourlastor.wilds_launcher.settings.SettingsState
import kotlinx.coroutines.flow.StateFlow

interface ViewModel<State> {

    fun start()

    fun stop()

    fun runPokeWilds(state: SettingsState.Loaded)

    val state: StateFlow<State>
}
