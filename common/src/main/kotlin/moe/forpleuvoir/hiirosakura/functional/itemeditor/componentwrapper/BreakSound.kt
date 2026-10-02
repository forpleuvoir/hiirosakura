package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.SoundEventEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.SoundPlayButton
import moe.forpleuvoir.hiirosakura.ui.widget.getSubtitle
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent

/**
 * 破坏音效组件行：展示框内左端是试听按钮、其后是音效字幕，动作格铅笔打开音效选择浮层。
 */
@Composable
fun BreakSoundComponentWrapper(
    key: Identifier,
    value: Holder<SoundEvent>,
    onValueChange: (Holder<SoundEvent>) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        tooltip = { Text(BuiltInRegistries.SOUND_EVENT.getKey(value.value())?.toString() ?: value.value().location().toString()) },
        leading = { SoundPlayButton(value) },
        onEdit = { showDialog = true },
    ) {
        Text(value.value().getSubtitle(), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    if (showDialog) {
        SoundEventEditorDialog(
            selected = value.value(),
            onSelect = { onValueChange(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(it)) },
            onDismissRequest = { showDialog = false },
        )
    }
}
