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
import net.minecraft.resources.Identifier
import moe.forpleuvoir.ibukigourd.ui.configwrapper.EnumSelector

@Composable
fun <E : Enum<E>> EnumComponentWrapper(
    key: Identifier,
    value: E,
    onValueChange: (E) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    EnumSelector(
        selected = value,
        onSelect = onValueChange,
        items = value.declaringJavaClass.enumConstants?.toList() ?: listOf(value),
        modifier = Modifier.fillMaxWidth(),
    )
}