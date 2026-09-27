package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import androidx.compose.foundation.layout.fillMaxWidth
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import androidx.compose.ui.graphics.RectangleShape

@Composable
fun DataComponentTypeSelector(
    selected: DataComponentType<*>,
    onSelect: (DataComponentType<*>) -> Unit,
    items: List<DataComponentType<*>> = BuiltInRegistries.DATA_COMPONENT_TYPE.toList(),
    itemEquals: (DataComponentType<*>, DataComponentType<*>) -> Boolean = { a, b -> a == b },
    content: @Composable (DataComponentType<*>) -> Unit = {
        Text(it.keyOrUnknown.toString())
    },
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (DataComponentType<*>, Boolean) -> Unit = { item, _ ->
        Text(item.keyOrUnknown.toString())
    },
    enabled: Boolean = true,
    searchFilter: ((String, DataComponentType<*>) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (DataComponentType<*>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (DataComponentType<*>) -> Unit)?)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding(),
) {
        OutlinedLabelBox(label = label, modifier = modifier, contentPadding = contentPadding) {
                Selector(
                selected = selected,
                onSelect = onSelect,
                items = items,
                itemEquals = itemEquals,
                content = content,
                itemContent = itemContent,
                enabled = enabled,
                itemLeadingIcon = itemLeadingIcon,
                itemTrailingIcon = itemTrailingIcon,
                searchFilter = searchFilter?.let { filter -> { item, query -> filter(query, item) } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
