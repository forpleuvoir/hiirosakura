package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.world.damagesource.DamageType

@Composable
fun DamageTypeComponentWrapper(
    key: Identifier,
    value: Holder<DamageType>,
    onValueChange: (Holder<DamageType>) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(
        modifier = Modifier.size(DataComponentEditorDefaults.entrySize),
        contentAlignment = Alignment.Center,
    ) {
        DamageTypeSelector(value.value(), { onValueChange(damageTypes.wrapAsHolder(it)) }, modifier = Modifier.fillMaxWidth())
    }
}


