package io.github.fourlastor.wilds_launcher.extension

fun Throwable.fullTrace(): String = """
        $message
        ${stackTraceToString()}
        ${cause?.also { "Caused by: ${it.fullTrace()}" }}
    """.trimIndent()
