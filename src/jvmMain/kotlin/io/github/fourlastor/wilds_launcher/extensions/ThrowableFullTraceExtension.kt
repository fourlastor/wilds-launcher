package io.github.fourlastor.wilds_launcher.extensions

fun Throwable.fullTrace(): String = """
        $message
        ${stackTraceToString()}
        ${cause?.also { "Caused by: ${it.fullTrace()}" }}
    """.trimIndent()