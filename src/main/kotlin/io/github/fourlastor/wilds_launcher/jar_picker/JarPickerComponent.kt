package io.github.fourlastor.wilds_launcher.jar_picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.wilds_launcher.extension.createScope
import io.github.fourlastor.wilds_launcher.file.LoadFileComponent
import io.github.fourlastor.wilds_launcher.navigation.Component
import io.github.fourlastor.wilds_launcher.releases.services.GitHubReleaseService
import io.github.fourlastor.wilds_launcher.settings.services.SettingsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class JarPickerComponent @AssistedInject constructor(
    @Assisted context: ComponentContext,
    @Assisted private val goToLauncher: () -> Unit,
    private val jarMatcher: JarMatcher,
    private val settingsService: SettingsService,
    private val releaseService: GitHubReleaseService,
) : Component, ComponentContext by context {

    private val scope = createScope()

    private val dialogNavigation = OverlayNavigation<DialogConfig>()
    private val dialog = childOverlay(
        source = dialogNavigation,
        childFactory = ::createDialogComponent
    )

    private fun createDialogComponent(
        screenConfig: DialogConfig,
        context: ComponentContext,
    ): Component = when (screenConfig) {
        is DialogConfig.PickJar -> LoadFileComponent(context) {
            if (it != null && jarMatcher.matches(it.name)) {
                onUpdateJar(it)
            }
            dialogNavigation.dismiss()
        }

        DialogConfig.DownloadLatest -> DownloadLatestComponent(context, releaseService) {
            onUpdateJar(it)
            dialogNavigation.dismiss()
        }
    }

    @Composable
    override fun render() {
        val dialog = dialog.subscribeAsState()
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                JarPicker(
                    downloadLatestRelease = ::downloadLatest,
                    findJar = ::findJar,
                    pickJar = ::pickJar,
                    modifier = Modifier.fillMaxWidth()
                )
                dialog.value.overlay?.instance?.render()
            }
        }
    }

    private fun downloadLatest() = dialogNavigation.activate(DialogConfig.DownloadLatest)

    private fun findJar() = scope.launch {
        val file = withContext(Dispatchers.IO) {
            releaseService.findInstallation()
        } ?: return@launch
        onUpdateJar(file)
    }

    private fun pickJar() = dialogNavigation.activate(DialogConfig.PickJar)

    private fun onUpdateJar(it: File) {
        settingsService.setJar(it.absolutePath)
        goToLauncher()
    }

    private sealed class DialogConfig : Parcelable {
        object PickJar : DialogConfig()
        object DownloadLatest : DialogConfig()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            context: ComponentContext,
            goToLauncher: () -> Unit,
        ): JarPickerComponent
    }
}
