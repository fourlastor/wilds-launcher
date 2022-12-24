import io.github.fourlastor.wilds_launcher.app.DaggerAppComponent
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    DaggerAppComponent.create().app().render()
}
