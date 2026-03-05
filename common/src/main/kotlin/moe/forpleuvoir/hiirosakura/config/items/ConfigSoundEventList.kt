package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.SoundEventSelector
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateTextWithParent
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.maxWidth
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.minWidth
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents

class ConfigSoundEventList(
    key: String,
    defaultValue: List<SoundEvent> = emptyList()
) : ConfigList<SoundEvent>(
    key,
    defaultValue,
    {
        it.serialization()
    }, {
        SoundEventDeserializer.deserialization(it)
    }
)

fun ConfigContainer.soundEventList(
    key: String,
    defaultValue: List<SoundEvent> = emptyList()
) = addConfig(ConfigSoundEventList(key, defaultValue))

fun SoundEvent.serialization(): SerializeElement =
    SerializePrimitive(this.location.toString())

object SoundEventDeserializer : Deserializer<SoundEvent> {
    override fun deserialization(serializeElement: SerializeElement): SoundEvent {
        return serializeElement.checkType<SerializePrimitive, SoundEvent> {
            SoundEvent.createVariableRangeEvent(Identifier.parse(it.asString))
        }.getOrThrow()
    }

}

fun ContainerScope.SoundEventListWrapper(
    config: ConfigSoundEventList,
    modifier: Modifier = Modifier,
    contentTableName: Text = HSLang.soundEffect,
    showIndex: Boolean = false
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {

        TableConfigListWrappedButton(
            config,
            title = config.translateTextWithParent(1, " → "),
            newValue = { SoundEvents.COPPER_PLACE },
            hoverTableScope = {
                ColumnBuilder {
                    Text(it.location.toString().take(120), modifier = Modifier.align(Alignment.CenterLeft).minWidth(80f).maxWidth(160f))
                }
            }
        ) {
            val recompose = { this@TableConfigListWrappedButton.executeRecompose() }
            MoveableTableHeader().MoveableTableColumCell(config, recompose, showIndex)

            Header(1) {
                Text(
                    contentTableName,
                    setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.minWidth(120f).maxWidth(250f).align(Alignment.Center)
                )
            }.Column { index, entry ->
                val state = mutableStateOf(entry).apply {
                    onSetValue = {
                        config.getValue()[index] = it
                        it
                    }
                }
                SoundEventSelector(state, modifier = Modifier.maxWidth(250f))
            }

            Header {
                Text(IGLang.edit)
            }.Column { index, _ ->
                DeleteButton(confirmMessage = {
                    IGLang.removeConfirm(
                        "[$index]${
                            Translatable(
                                "subtitles.${config[index].location.path}",
                                config[index].location.toString()
                            ).plainText
                        }"
                    )
                }, recompose = recompose) {
                    config.removeAt(index)
                }
            }
        }

        ConfigResetButton(config) {
            this@Row.executeRecompose()
        }
    }
}
