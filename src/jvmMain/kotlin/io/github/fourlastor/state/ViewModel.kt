package io.github.fourlastor.state

import kotlinx.coroutines.flow.StateFlow

interface ViewModel<State> {

    fun start()

    fun stop()

    val state: StateFlow<State>
}
