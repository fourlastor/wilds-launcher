package io.github.fourlastor.wilds_launcher

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipFile

const val OWNER = "SheerSt"
const val REPOSITORY = "pokewilds"

const val READ_SIZE_IN_BYTES = 1024
const val BUFFER_SIZE = 4096

fun getLatestVersion() : String? {
    val url = URL("https://api.github.com/repos/$OWNER/$REPOSITORY/releases/latest")
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

    val filenameWithoutExtension = "pokemon-wilds-windows64"
    val filename = "$filenameWithoutExtension.zip"

    val url = URL("https://github.com/$OWNER/$REPOSITORY/releases/download/$latestVersion/$filename")
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

    println("Unzipping file...")

    val destination = filenameWithoutExtension

    File(destination).run {
        if (!exists()) {
            mkdirs()
        }
    }

    ZipFile(file).use { zipFile ->
        zipFile.entries().asSequence().forEach { entry ->
            zipFile.getInputStream(entry).use { inputStream ->
                val path = destination + File.separator + entry.name

                if (entry.isDirectory) {
                    File(path).mkdir()
                }
                else {
                    BufferedOutputStream(FileOutputStream(path)).use { outputStream ->
                        val bytes = ByteArray(BUFFER_SIZE)
                        var readBytes: Int

                        while (inputStream.read(bytes).also { readBytes = it } != -1) {
                            outputStream.write(bytes, 0, readBytes)
                        }
                    }
                }
            }
        }
    }

    println("Unzipped file.")

    file.delete()
}