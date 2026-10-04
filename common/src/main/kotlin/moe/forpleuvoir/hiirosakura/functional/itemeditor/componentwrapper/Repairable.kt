package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.HolderSetItemEditorDialog
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Repairable

@Composable
fun RepairableComponentWrapper(
    key: Identifier,
    value: Repairable,
    onValueChange: (Repairable) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    val count = value.items.count()
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.thenIf(count > 0) {
            Modifier.tooltip {
                FlowRow(
                    modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    value.items.take(20).forEach { item ->
                        ItemIcon(ItemStack(item), scaleOnHover = 1f, showTooltip = false)
                    }
                }
            }
        },
        onEdit = { showDialog = true },
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            value.items.take(6).forEach { item ->
                ItemIcon(ItemStack(item), size = DpSize(32.dp, 32.dp), scaleOnHover = 1f, showTooltip = false)
            }

            if (count > 6)
                Text("...", overflow = TextOverflow.Ellipsis, maxLines = 1)
            if (count == 0)
                Text(component = IGLang.Misc.hasNothing, overflow = TextOverflow.Ellipsis, maxLines = 1)
        }
    }

    if (showDialog) {
        HolderSetItemEditorDialog(
            value = value.items,
            onValueChange = { onValueChange(Repairable(it)) },
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

