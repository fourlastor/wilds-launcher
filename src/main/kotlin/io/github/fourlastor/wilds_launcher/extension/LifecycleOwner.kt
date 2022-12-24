package io.github.fourlastor.wilds_launcher.extension

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

fun LifecycleOwner.createScope() = CoroutineScope(Dispatchers.Default + Job()).also {
    lifecycle.doOnDestroy { it.cancel() }
}
