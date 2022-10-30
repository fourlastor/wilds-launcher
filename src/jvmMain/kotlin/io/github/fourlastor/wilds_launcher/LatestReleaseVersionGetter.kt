package io.github.fourlastor.wilds_launcher

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.net.HttpURLConnection
import java.net.URL

fun getLatestReleaseVersion() : String? {
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