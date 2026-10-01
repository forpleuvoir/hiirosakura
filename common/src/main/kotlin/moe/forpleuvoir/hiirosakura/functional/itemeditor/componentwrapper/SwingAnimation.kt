package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import net.minecraft.resources.Identifier
import net.minecraft.world.item.SwingAnimationType
import net.minecraft.world.item.component.SwingAnimation
import moe.forpleuvoir.ibukigourd.ui.configwrapper.EnumSelector
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun SwingAnimationComponentWrapper(
    key: Identifier,
    value: SwingAnimation,
    onValueChange: (SwingAnimation) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        onEdit = { showDialog = true },
    ) {
        Text(
            "type ${value.type.serializedName} · duration ${value.duration}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

    if (showDialog) {
        SwingAnimationEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

@Composable
fun SwingAnimationEditorDialog(
    key: Identifier,
    value: SwingAnimation,
    onValueChange: (SwingAnimation) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var editing by remember { mutableStateOf(value) }
    SimpleAlertDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(editing)
            true
        },
        title = title,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DataComponentField(key, suffix = "type", fallback = "Type", modifier = Modifier.fillMaxWidth()) {
                    EnumSelector(
                        selected = editing.type,
                        onSelect = { editing = SwingAnimation(it, editing.duration) },
                        items = SwingAnimationType.entries,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DataComponentField(key, suffix = "duration", fallback = "Duration", modifier = Modifier.fillMaxWidth()) {
                    IntField(
                        editing.duration,
                        { editing = SwingAnimation(editing.type, it) },
                        valueRange = 1..Int.MAX_VALUE,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    )
}