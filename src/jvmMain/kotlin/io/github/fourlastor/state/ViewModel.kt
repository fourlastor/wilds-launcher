package io.github.fourlastor.state

import io.github.fourlastor.settings.SettingsState
import kotlinx.coroutines.flow.StateFlow

interface ViewModel<State> {

    fun start()

    fun stop()

    fun runPokeWilds(state: SettingsState.Loaded)

    val state: StateFlow<State>
}
