package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatField
import net.minecraft.resources.Identifier
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import androidx.compose.foundation.layout.fillMaxWidth
import moe.forpleuvoir.hiirosakura.ui.compat.NumberFieldStyle
import moe.forpleuvoir.hiirosakura.ui.compat.LocalNumberFieldStyle

@Composable
fun FloatComponentWrapper(
    key: Identifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    valueToText: (Float) -> String = { it.toString() },
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                LabelBox(label = { Text("Float [$valueRange]") }, modifier = Modifier.size(DataComponentEditorDefaults.entrySize)) {
            FloatField(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                valueToText = valueToText,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}