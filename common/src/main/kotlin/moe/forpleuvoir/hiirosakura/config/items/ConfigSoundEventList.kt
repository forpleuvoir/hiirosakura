package moe.forpleuvoir.hiirosakura.config.items

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.SoundEventSelectorInnerEditor
import moe.forpleuvoir.hiirosakura.ui.widget.getSubtitle
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigDialogDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigDialogTitle
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigListRow
import moe.forpleuvoir.ibukigourd.ui.configwrapper.asDerivedState
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentList
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
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

/**
 * 音效列表：行上显示条目数，点开在浮层里编辑（增删 + 拖拽排序）。
 *
 * 条目控件是 [SoundEventSelectorInnerEditor]（框内左端试听、右端编辑）；新增条目取空音效
 * [SoundEvents.EMPTY]，再由条目自己的编辑按钮选音效。
 *
 * @param config 音效列表配置项
 * @param modifier 作用于配置页上那一行
 */
@Composable
fun SoundEventListConfigWrapper(
    config: ConfigList<SoundEvent>,
    modifier: Modifier = Modifier,
) {
    var editing by remember(config) { mutableStateOf(false) }
    val size by config.asDerivedState { it.size }

    ConfigListRow(config, IGLang.ConfigWrapper.listConfigWrapperText(size), modifier) { editing = true }

    if (editing) {
        SoundEventListEditDialog(config, onDismiss = { editing = false })
    }
}

/**
 * 音效列表编辑浮层：内容列 + 拖拽排序 + 右下浮动新增；编辑在副本上进行，确认时才整体写回。
 *
 * 删除按钮的确认标题用音效名（`SoundEvent.toString()` 是数据类转储，读不出音效）。
 *
 * @param config 音效列表配置项
 * @param onDismiss 关闭浮层
 */
@Composable
private fun SoundEventListEditDialog(
    config: ConfigList<SoundEvent>,
    onDismiss: () -> Unit,
) {
    val keyed = rememberKeyedList(config.getValue(), key = config)

    FlexibleDialog(
        onDismissRequest = onDismiss,
        onConfirmRequest = {
            config.setValue(keyed.entries.values())
            true
        },
        title = { ConfigDialogTitle(config) },
        minWidth = ConfigDialogDefaults.MinWidth,
        maxHeight = ConfigDialogDefaults.MaxHeight,
        content = {
            EditDialogContent(
                modifier = Modifier.width(ConfigDialogDefaults.ContentWidth),
                // 表头交给表格自己（列宽与单元格天然对齐），这里不再叠一层
                header = {},
                addButton = {
                    Button(
                        onClick = { keyed.add(SoundEvents.EMPTY) },
                        contentPadding = ConfigControlDefaults.IconButtonPadding,
                    ) {
                        Icon(Icons.Add, scale = configIconScale())
                    }
                },
            ) { listState ->
                EditDialogContentList(
                    state = keyed,
                    lazyListState = listState,
                    removeButton = { index, sound ->
                        RemoveConfirmButton(
                            message = sound.getSubtitle().plainText,
                            onConfirm = { keyed.removeAt(index) },
                            iconScale = LocalIconScale.current,
                            contentPadding = EditDialogContentDefaults.iconPadding,
                        )
                    },
                    columns = {
                        column(
                            width = ConfigDialogDefaults.FillColumnWidth,
                            alignment = Alignment.CenterStart,
                            header = { Text(component = HSLang.Common.soundEffect) },
                        ) { index, entry ->
                            SoundEventSelectorInnerEditor(
                                value = entry.value,
                                onValueChange = { keyed.setValue(index, it) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    },
                )
            }
        },
    )
}
