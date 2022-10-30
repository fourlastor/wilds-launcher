package io.github.fourlastor.wilds_launcher

import io.github.fourlastor.wilds_launcher.operating_system.OperatingSystem
import io.github.fourlastor.wilds_launcher.operating_system.getOperatingSystem
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

const val READ_SIZE_IN_BYTES = 1024

fun downloadLatestRelease(onProgressChanged: (Float) -> Unit) : File? {
    val version = getLatestReleaseVersion()

    if (version == null) {
        println("Could not get latest version.")
        return null
    }

    val filenameWithoutExtension = getFilenameWithoutExtension()
    val filename = "$filenameWithoutExtension.zip"

    val url = URL("https://github.com/SheerSt/pokewilds/releases/download/$version/$filename")
    val connection = url.openConnection()
    val totalBytesToDownload = connection.contentLength

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

    val destination = File(filenameWithoutExtension)
    unzipArchive(file, destination)

    println("Unzipped archive.")

    return destination
}

private fun getFilenameWithoutExtension() : String {
    // TODO: What is the difference?

    //val filenameWithoutExtension = "pokewilds-"
    val filenameWithoutExtension = "pokemon-wilds-"

    return when (getOperatingSystem()) {
        OperatingSystem.Windows -> filenameWithoutExtension + "windows64"
        OperatingSystem.Linux -> filenameWithoutExtension + "linux64"
        else -> filenameWithoutExtension + "otherplatforms"
    }
}