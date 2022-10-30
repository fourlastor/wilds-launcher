package io.github.fourlastor.wilds_launcher

import java.util.*

fun getOperatingSystem() : OperatingSystem {
    val name = System.getProperty("os.name").lowercase(Locale.getDefault())

    return when {
        name.contains("win") -> OperatingSystem.Windows
        name.contains("nix") || name.contains("nux") || name.contains("aix") -> OperatingSystem.Linux
        else -> OperatingSystem.Other
    }
}