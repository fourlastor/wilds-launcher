package io.github.fourlastor.wilds_launcher.releases.services

import io.github.fourlastor.wilds_launcher.operating_system.OperatingSystem
import io.github.fourlastor.wilds_launcher.operating_system.getOperatingSystem
import io.github.fourlastor.wilds_launcher.unzipArchive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

const val READ_SIZE_IN_BYTES = 1024

class GitHubReleaseService : ReleaseService {
    override fun getLatestReleaseVersion() : String? {
        return getLatestReleaseProperty("name")
    }

    override fun getLatestReleaseChangelog() : String? {
        return getLatestReleaseProperty("body")
    }

    override fun getLatestReleaseSizeInBytes(): Long? {
        val version = getLatestReleaseVersion() ?: return null
        val connection = getURL(version).openConnection()

        return connection.contentLength.toLong()
    }

    override fun downloadLatestRelease(onProgressChanged: (Float) -> Unit) : File? {
        val version = getLatestReleaseVersion()

        if (version == null) {
            println("Could not get latest version.")
            return null
        }

        val connection = getURL(version).openConnection()
        val totalBytesToDownload = connection.contentLength

        val filename = getFilename()
        val file = File(filename)

        println("Starting download of latest release.")

        BufferedInputStream(connection.getInputStream()).use { bufferedInputStream ->
            FileOutputStream(filename).use { fileOutputStream ->
                val bytes = ByteArray(READ_SIZE_IN_BYTES)

                var downloadedBytes: Int
                var totalBytesDownloaded = 0

                while (bufferedInputStream.read(bytes, 0, READ_SIZE_IN_BYTES).also { downloadedBytes = it } != -1) {
                    fileOutputStream.write(bytes, 0, downloadedBytes)
                    totalBytesDownloaded += downloadedBytes

                    val progress = (totalBytesDownloaded.toFloat() / totalBytesToDownload.toFloat())
                    println("$totalBytesDownloaded/$totalBytesToDownload (${kotlin.math.floor(progress * 100).toInt()}%) bytes downloaded...")

                    onProgressChanged(progress)
                }
            }
        }

        println("Finished download of latest release.")

        println("Unzipping archive...")

        val filenameWithoutExtension = getFilenameWithoutExtension()
        val destination = File(filenameWithoutExtension)

        unzipArchive(file, destination)

        println("Unzipped archive.")

        return destination
    }

    private fun getLatestReleaseProperty(name: String) : String? {
        val url = URL("https://api.github.com/repos/SheerSt/pokewilds/releases/latest")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.setRequestProperty("accept", "application/vnd.github+json")

        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        if (connection.responseCode >= 300) {
            return null
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val jsonObject = Json.parseToJsonElement(response) as JsonObject

        val jsonPrimitive = jsonObject[name] as JsonPrimitive
        val content = jsonPrimitive.content

        return content
    }

    private fun getFilenameWithoutExtension() : String {
        val filenameWithoutExtension = "pokewilds-"

        return when (getOperatingSystem()) {
            OperatingSystem.Windows -> filenameWithoutExtension + "windows64"
            OperatingSystem.Linux -> filenameWithoutExtension + "linux64"
            else -> filenameWithoutExtension + "otherplatforms"
        }
    }

    private fun getFilename() : String {
        return "${getFilenameWithoutExtension()}.zip"
    }

    private fun getURL(version: String) : URL {
        return URL("https://github.com/SheerSt/pokewilds/releases/download/$version/${getFilename()}")
    }
}