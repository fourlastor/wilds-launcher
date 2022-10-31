package io.github.fourlastor.wilds_launcher.releases.services

import java.io.File

interface ReleaseService {
    fun getLatestReleaseVersion() : String?
    fun getLatestReleaseChangelog() : String?

    fun downloadLatestRelease(onProgressChanged: (Float) -> Unit) : File?
}