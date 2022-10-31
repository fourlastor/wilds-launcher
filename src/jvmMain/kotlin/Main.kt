import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fourlastor.wilds_launcher.Context
import io.github.fourlastor.wilds_launcher.releases.GitHubReleaseService
import io.github.fourlastor.wilds_launcher.releases.ReleaseService
import io.github.fourlastor.wilds_launcher.settings.*
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

    val dirs = Dirs(AppDirsFactory.getInstance())
    val configFile = dirs.getConfigFile()

    val settingsService: SettingsService = FileSettingsService(configFile)
    settingsService.load()

    val releaseService: ReleaseService = GitHubReleaseService()

    val context = Context(coroutineScope, settingsService, releaseService)

    var logs = ""

    application {
        Window(title = TITLE, onCloseRequest = {
            coroutineScope.cancel()

            if (logs.isNotBlank()) {
                File("logs.log").writeText(logs)
            }

            exitApplication()
        }) {
            StateMachine(
                context = context,
                getPokeWildsLocation = { pickFile() },
                log = {
                    logs += "\n" + it
                    println(it)
                }
            )
        }
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
