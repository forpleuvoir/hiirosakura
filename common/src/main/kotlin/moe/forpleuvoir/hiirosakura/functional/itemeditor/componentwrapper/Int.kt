package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.ibukigourd.ui.preset.IntField
import moe.forpleuvoir.ibukigourd.ui.preset.LocalNumberFieldStyle
import moe.forpleuvoir.ibukigourd.ui.preset.NumberFieldStyle
import net.minecraft.resources.Identifier

@Composable
fun IntComponentWrapper(
    key: Identifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    valueToText: (Int) -> String = { it.toString() },
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
        IntField(
            value = value,
            onValueChange = onValueChange,
            range = valueRange,
            valueToText = valueToText,
            labelPosition = TextFieldLabelPosition.Attached(true),
            label = { Text("Int [$valueRange]") },
            modifier = Modifier.size(DataComponentEditorDefaults.entrySize),
        )
    }
}