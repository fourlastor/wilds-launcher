import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fourlastor.App
import io.github.fourlastor.AppComponent
import io.github.fourlastor.settings.Dirs
import io.github.fourlastor.settings.SettingsRepository
import io.github.fourlastor.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import net.harawata.appdirs.AppDirsFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.awt.FileDialog

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
            App(AppComponent()) { pickFile() }
        }
    }
}


private fun FrameWindowScope.pickFile(): Pair<String, String>? {
    FileDialog(window).apply {
        isVisible = true
        val file = file ?: return@apply
        val directory = directory ?: return@apply

        return directory to file
    }
    return null
}
