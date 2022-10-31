import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fourlastor.wilds_launcher.settings.*
import io.github.fourlastor.wilds_launcher.ui.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import net.harawata.appdirs.AppDirsFactory
import java.awt.FileDialog

const val TITLE = "PokeWilds Launcher"

fun main() {
    val dirs = Dirs(AppDirsFactory.getInstance())
    val scope = CoroutineScope(Dispatchers.Default)

    val configFile = dirs.getConfigFile()
    val settings = loadSettings(configFile) ?: Settings()

    var logs = ""

    application {
        Window(title = TITLE, onCloseRequest = {
            scope.cancel()
            exitApplication()
        }) {
            StateMachine(
                settings,
                scope = scope,
                getPokeWildsLocation = { pickFile() },
                devMode = settings.devMode,
                onDevModeChanged = {
                    settings.devMode = it
                    settings.save(configFile)
                },
                saveWildsDir = { dir, jar ->
                    settings.dir = dir
                    settings.jar = jar

                    settings.save(configFile)
                },
                clearData = {
                    settings.clear()
                    configFile.delete()
                },
                log = { message ->
                    logs += "\n" + message
                    println(message)
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
