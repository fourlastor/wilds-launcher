package io.github.fourlastor.wilds_launcher.releases

import java.io.File

interface ReleaseService {
    fun getLatestReleaseVersion() : String?
    fun getLatestReleaseChangelog() : String?

    fun downloadLatestRelease(onProgressChanged: (Float) -> Unit) : File?
}