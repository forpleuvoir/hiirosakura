package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.HolderSetDamageTypeEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.translatableText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.DamageResistant

@Composable
fun DamageResistantComponentWrapper(
    key: Identifier,
    value: DamageResistant,
    onValueChange: (DamageResistant) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    val count = value.types.count()
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.thenIf(count != 0) {
            Modifier.tooltip {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    value.types.take(10).forEach { type ->
                        Text(type.value().translatableText)
                    }
                }
            }
        },
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(count), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    if (showDialog) {
        HolderSetDamageTypeEditorDialog(
            value = value.types,
            onValueChange = { onValueChange(DamageResistant(it)) },
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

