import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fourlastor.settings.Dirs
import io.github.fourlastor.settings.SettingsRepository
import io.github.fourlastor.settings.SettingsState
import io.github.fourlastor.settings.SettingsViewModel
import io.github.fourlastor.state.ViewModelContainer
import io.github.fourlastor.ui.JarPicker
import io.github.fourlastor.ui.Launcher
import kotlinx.coroutines.Dispatchers
import net.harawata.appdirs.AppDirsFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.awt.FileDialog
import java.io.File

@Composable
@Preview
fun App(appComponent: AppComponent, pickFile: () -> Pair<String, String>?) {
    val viewModel = appComponent.settingsViewModel
    MaterialTheme {
        ViewModelContainer(viewModel) { settingsState ->
            when (settingsState) {
                is SettingsState.Loaded -> Launcher(
                    settingsState = settingsState,
                    onDevModeChanged = {
                        viewModel.devMode(it)
                    },
                    onAngleGles20Changed = {
                        viewModel.angleGles20(it)
                    },
                    runPokeWilds = {
                        runPokeWilds(it)
                    }
                )
                is SettingsState.Missing -> JarPicker(pickFile, viewModel)
            }
        }
    }
}

fun main() {
    val module = module {
        single { AppDirsFactory.getInstance() }
        single { Dirs(get()) }
        single { Dispatchers }
        single { SettingsRepository(get(), get()) }
        single { SettingsViewModel(get(), get()) }
    }
    startKoin {
        printLogger()
        modules(module)
    }
    application {
        Window(title = "PokeWilds launcher", onCloseRequest = ::exitApplication) {
            App(AppComponent()) { getPokeWildsLocation() }
        }
    }
}

private fun runPokeWilds(state: SettingsState.Loaded) {
    val runArgs = mutableListOf("java", "-jar", state.jar).apply {
        if (state.angleGles20) {
            add("angle_gles20")
        }
        if (state.devMode) {
            add("dev")
        }
    }.toTypedArray()
    Runtime.getRuntime().exec(
        runArgs,
        null,
        File(state.dir)
    )
}

private fun FrameWindowScope.getPokeWildsLocation(): Pair<String, String>? {
    val userConfigDir = AppDirsFactory.getInstance().getUserConfigDir(
        "wilds-launcher",
        "1.0.0",
        "io.github.fourlastor"
    )
    println(userConfigDir)

    FileDialog(window).apply {
        isVisible = true
        val file = file ?: return@apply
        val directory = directory ?: return@apply

        return directory to file
    }
    return null
}

class AppComponent : KoinComponent {
    val settingsViewModel by inject<SettingsViewModel>()
}
