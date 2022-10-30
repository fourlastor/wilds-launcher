package io.github.fourlastor.wilds_launcher

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

fun unzipArchive(archive: File, destination: File, deleteArchiveAfterwards: Boolean = true) {
    destination.run {
        if (!exists()) {
            mkdirs()
        }
    }

    ZipFile(archive).use { zipFile ->
        zipFile.entries().asSequence().forEach { entry ->
            zipFile.getInputStream(entry).use { inputStream ->
                val path = destination.absolutePath + File.separator + entry.name

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

    if (deleteArchiveAfterwards) {
        archive.delete()
    }
}