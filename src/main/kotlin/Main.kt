import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fourlastor.wilds_launcher.Context
import io.github.fourlastor.wilds_launcher.logs.FileLogger
import io.github.fourlastor.wilds_launcher.logs.Logger
import io.github.fourlastor.wilds_launcher.releases.services.GitHubReleaseService
import io.github.fourlastor.wilds_launcher.releases.services.ReleaseService
import io.github.fourlastor.wilds_launcher.settings.services.FileSettingsService
import io.github.fourlastor.wilds_launcher.settings.services.SettingsService
import io.github.fourlastor.wilds_launcher.ui.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import net.harawata.appdirs.AppDirsFactory
import java.awt.FileDialog
import java.io.File

const val TITLE = "PokeWilds Launcher"

const val APP_NAME = "wilds-launcher"
const val APP_VERSION = "1.0.0"
const val APP_AUTHOR = "io.github.fourlastor"

fun main() {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val appDirs = AppDirsFactory.getInstance()

    val configDirectory = appDirs.getUserConfigDir(APP_NAME, APP_VERSION, APP_AUTHOR)
    val dataDirectory = appDirs.getUserDataDir(APP_NAME, APP_VERSION, APP_AUTHOR)

    println(configDirectory)
    println(dataDirectory)

    val settingsFile = File(configDirectory, "settings.json")

    val settingsService: SettingsService = FileSettingsService(settingsFile)
    settingsService.load()

    val installDirectory = File(dataDirectory)
    val releaseService: ReleaseService = GitHubReleaseService(installDirectory)

    val logFile = File("logs.log")
    val logger: Logger = FileLogger(logFile)

    val context = Context(coroutineScope, settingsService, releaseService, logger)

    application {
        Window(title = TITLE, onCloseRequest = {
            coroutineScope.cancel()
            logger.shutdown()

            exitApplication()
        }) { StateMachine(context) { pickFile() } }
    }
}


fun FrameWindowScope.pickFile(): Pair<String, String>? {
    FileDialog(window).apply {
        isVisible = true
        val file = file ?: return@apply
        val directory = directory ?: return@apply

        return directory to file
    }
    return null
}
