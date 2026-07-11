package moe.forpleuvoir.hiirosakura.config.items

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.SoundEventBrowser
import moe.forpleuvoir.hiirosakura.ui.widget.SoundEventSelector
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ListConfigWrapperDefaults
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigList
import moe.forpleuvoir.nebula.config.item.configList
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents

context(group: ConfigGroup)
fun configSoundEventList(name: String, defaultValue: List<SoundEvent>) = configList(name, defaultValue, SoundEventCodec)

object SoundEventCodec : Codec<SoundEvent> {
    override fun serialization(target: SoundEvent): SerializeElement = SerializePrimitive(target.location.toString())
    override fun deserialization(data: SerializeElement): Result<SoundEvent> = DeserializationException.runCatching {
        data.checkType<SerializePrimitive, SoundEvent> {
            SoundEvent.createVariableRangeEvent(Identifier.parse(it.value.requireType()))
        }
    }
}

//------------ UI Wrapper 需要播放按钮 ------------\\

@Composable
fun SoundEventListConfigWrapper(
    config: ConfigList<SoundEvent>,
    editorDialogTitle: @Composable (() -> Unit)? = {
        Text(InlineStyleText(config.translateText.plainText))
    },
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier.padding(40.dp).size(720.dp, 800.dp),
) = ListConfigWrapperDefaults.run {
    CompositionLocalProvider(
        ItemBrowserDefaults.LocalItemIconSize provides 32.dp
    ) {
        var showEditDialog by remember { mutableStateOf(false) }
        RowWrapper(
            config = config,
            modifier = modifier
        ) { showEditDialog = true }
        if (showEditDialog) {
            val editingValue = rememberKeyedList(config)
            var nextKey by remember { mutableLongStateOf(editingValue.size.toLong()) }
            EditDialog(
                config = config,
                editingValue = editingValue,
                modifier = dialogModifier,
                title = editorDialogTitle,
                onDismissRequest = { showEditDialog = false },
                onConfirmRequest = {
                    config.clear()
                    config.addAll(it.values())
                    true
                }
            ) {
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
                        EditDialogContentHeader(
                            contentHeader = { Text(HSLang.Common.soundEffect) }
                        )
                    },
                    addDialog = { onDismissRequest ->
                        var soundEvent by remember { mutableStateOf(SoundEvents.EMPTY) }
                        SimpleAlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(IGLang.Misc.add) },
                            onConfirmRequest = {
                                editingValue.add(Keyed(nextKey++, soundEvent))
                                true
                            },
                            content = {
                                SoundEventSelector(soundEvent, { soundEvent = it }, modifier = Modifier.fillMaxWidth())
                            }
                        )
                    }
                ) { lazyListState ->
                    EditDialogContentList(
                        data = editingValue,
                        key = { it.key },
                        modifier = Modifier,
                        lazyListState = lazyListState
                    ) { keyed, onValueChange ->
                        SoundEventSelector(keyed.value, { onValueChange(keyed.copyValue(it)) }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}