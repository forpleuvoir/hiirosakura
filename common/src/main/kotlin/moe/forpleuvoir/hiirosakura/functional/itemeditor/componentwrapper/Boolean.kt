package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import net.minecraft.resources.Identifier

@Composable
fun BooleanComponentWrapper(
    key: Identifier,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Switch(
            value,
            onValueChange
        )
    }
}