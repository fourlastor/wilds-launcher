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

fun main() {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val appDirs = AppDirsFactory.getInstance()
    val config = appDirs.getUserConfigDir(
        "wilds-launcher",
        "1.0.0",
        "io.github.fourlastor"
    )
    val configFile = File(config, "config.json")

    val settingsService: SettingsService = FileSettingsService(configFile)
    settingsService.load()

    val releaseService: ReleaseService = GitHubReleaseService()

    val logger: Logger = FileLogger(File("logs.log"))

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
