package io.github.fourlastor.wilds_launcher.operating_system

import java.util.*

fun getOperatingSystem() : OperatingSystem {
    val name = System.getProperty("os.name").lowercase(Locale.getDefault())

    return when {
        name.contains("win") -> OperatingSystem.Windows
        name.contains("nix") || name.contains("nux") || name.contains("aix") -> OperatingSystem.Linux
        name.contains("mac") -> OperatingSystem.Mac
        else -> OperatingSystem.Other
    }
}