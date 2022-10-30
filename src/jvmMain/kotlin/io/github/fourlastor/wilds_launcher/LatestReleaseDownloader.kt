package io.github.fourlastor.wilds_launcher

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

const val READ_SIZE_IN_BYTES = 1024

fun getLatestVersion() : String? {
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

    val jsonPrimitive = jsonObject["name"] as JsonPrimitive
    val version = jsonPrimitive.content

    return version
}

fun downloadLatestRelease() {
    val latestVersion = getLatestVersion()

    if (latestVersion == null) {
        println("Could not get latest version.")
        return
    }

    val filename = "pokemon-wilds-windows64.zip"
    val url = URL("https://github.com/SheerSt/pokewilds/releases/download/$latestVersion/$filename")
    val connection = url.openConnection()
    val totalBytesToDownload = connection.contentLength

    val file = File(filename)

    if (file.exists()) {
        if (file.length().toInt() != totalBytesToDownload) {
            println("The latest release has a different file size than the currently downloaded one. Your current might be corrupt?")
        }
        else {
            println("The latest release is already downloaded.")
            return
        }
    }

    println("Starting download of latest release.")

    BufferedInputStream(connection.getInputStream()).use { bufferedInputStream ->
        FileOutputStream(filename).use { fileOutputStream ->
            val bytes = ByteArray(READ_SIZE_IN_BYTES)

            var downloadedBytes: Int
            var totalBytesDownloaded = 0

            while (bufferedInputStream.read(bytes, 0, READ_SIZE_IN_BYTES).also { downloadedBytes = it } != -1) {
                fileOutputStream.write(bytes, 0, downloadedBytes)
                totalBytesDownloaded += downloadedBytes

                println("$totalBytesDownloaded/$totalBytesToDownload (${kotlin.math.floor((totalBytesDownloaded.toFloat() / totalBytesToDownload.toFloat()) * 100).toInt()}%) bytes downloaded...")
            }
        }
    }

    println("Finished download of latest release.")
}