package io.github.fourlastor.wilds_launcher.jar_picker

import javax.inject.Inject

class JarMatcher @Inject constructor() {

    fun matches(name: String): Boolean = name == FILENAME || name == FILENAME_ALT

    companion object {
        const val FILENAME = "pokewilds.jar"
        const val FILENAME_ALT = "pokemon-wilds.jar"
    }
}
