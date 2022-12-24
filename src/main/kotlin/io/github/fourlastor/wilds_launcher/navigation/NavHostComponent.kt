package io.github.fourlastor.wilds_launcher.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.wilds_launcher.jar_picker.JarPickerComponent

@OptIn(ExperimentalDecomposeApi::class)
class NavHostComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val jarPickerFactory: JarPickerComponent.Factory,
) : Component, ComponentContext by componentContext {
    private val navigation = StackNavigation<ScreenConfig>()
    private val stack = childStack(
        source = navigation,
        initialConfiguration = ScreenConfig.JarPicker,
        childFactory = ::createScreenComponent
    )

    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        context: ComponentContext,
    ): Component = when (screenConfig) {
        is ScreenConfig.JarPicker -> jarPickerFactory.create(context)
    }

    @Composable
    override fun render() {
        Children(stack = stack) {
            it.instance.render()
        }
    }

    private sealed class ScreenConfig : Parcelable {
        object JarPicker : ScreenConfig()
    }

    @AssistedFactory
    interface Factory {
        fun create(context: ComponentContext): NavHostComponent
    }
}
