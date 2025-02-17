package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.ConfigSoundEventList
import moe.forpleuvoir.hiirosakura.gui.widget.SoundEventSelector
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.execute
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.recompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigColumnWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ListConfigWrappedButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveableListConfigEntryWrapper
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import net.minecraft.sound.SoundEvents

fun WidgetContainerScope.SoundEffectListWrapper(
    config: ConfigSoundEventList,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        ListConfigWrappedButton(
            config = config,
            newValue = { SoundEvents.BLOCK_COPPER_PLACE },
            hoverEntryToString = { it.id.toString() }
        ) { entry, index ->
            MoveableListConfigEntryWrapper(
                config = config,
                index = index,
                recompose = { execute { this@ListConfigWrappedButton.recompose() } }
            ) {
                val state = mutableStateOf(entry).apply {
                    onSetValue = {
                        config.getValue()[index] = it
                        it
                    }
                }
                SoundEventSelector(state)
            }
        }
        ConfigResetButton(config) {
            execute { this@Column.recompose() }
        }
    }
}
