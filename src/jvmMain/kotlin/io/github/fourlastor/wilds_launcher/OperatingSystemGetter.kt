package io.github.fourlastor.wilds_launcher

fun getOperatingSystem() : OperatingSystem {
    val name = System.getProperty("os.name").toLowerCase()

    return when {
        name.contains("win") -> OperatingSystem.Windows
        name.contains("nix") || name.contains("nux") || name.contains("aix") -> OperatingSystem.Linux
        else -> OperatingSystem.Other
    }
}