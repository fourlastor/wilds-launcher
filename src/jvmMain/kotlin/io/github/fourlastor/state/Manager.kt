package io.github.fourlastor.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Manager<State> constructor(
    initialState: State,
) {
    private val _state = MutableStateFlow(initialState)

    val state: StateFlow<State> = _state

    fun update(action: (State) -> State) {
        _state.value = action(_state.value)
    }
}
