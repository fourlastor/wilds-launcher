package io.github.fourlastor.wilds_launcher

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

const val READ_SIZE_IN_BYTES = 1024

fun downloadLatestRelease() {
    val version = "v0.8.6"
    val filename = "pokemon-wilds-windows64.zip"
    val url = URL("https://github.com/SheerSt/pokewilds/releases/download/$version/$filename")
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