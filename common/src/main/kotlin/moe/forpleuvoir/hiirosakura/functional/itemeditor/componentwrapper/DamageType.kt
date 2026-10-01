package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
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
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    DamageTypeSelector(value.value(), { onValueChange(damageTypes.wrapAsHolder(it)) }, modifier = Modifier.fillMaxWidth())
}


