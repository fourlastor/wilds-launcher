import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fourlastor.settings.*
import io.github.fourlastor.state.ViewModelContainer
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
        ViewModelContainer(viewModel) {
            when (it) {
                is SettingsState.Loading -> return@ViewModelContainer
                is SettingsState.Loaded -> {
                    Button({
                        runPokewilds(it.dir, it.jar)
                    }) {
                        Text("Starts Pokewilds")
                    }
                }
                is SettingsState.Missing -> {
                    Button({
                        pickFile()?.also { (dir, jar) ->
                            viewModel.saveWildsDir(dir, jar)
                        }
                    }) {
                        Text("Find pokewilds.jar file")
                    }
                }
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
        Window(onCloseRequest = ::exitApplication) {
            App(AppComponent()) { startPokewilds() }
        }
    }
}

fun runPokewilds(directory: String, jarFile: String) {
    Runtime.getRuntime().exec(
        arrayOf("java", "-jar", jarFile),
        null,
        File(directory)
    )
}

fun FrameWindowScope.startPokewilds(): Pair<String, String>? {
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
