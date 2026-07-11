package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.ibukigourd.ui.preset.Selector
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DataComponentTypeSelector(
    selected: DataComponentType<*>,
    onSelect: (DataComponentType<*>) -> Unit,
    items: List<DataComponentType<*>> = BuiltInRegistries.DATA_COMPONENT_TYPE.toList(),
    itemEquals: (DataComponentType<*>, DataComponentType<*>) -> Boolean = { a, b -> a == b },
    content: @Composable (DataComponentType<*>) -> Unit = {
        Text(it.keyOrUnknown.toString())
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(),
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
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
) = Selector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    itemEquals = itemEquals,
    content = content,
    labelPosition = labelPosition,
    label = label,
    itemContent = itemContent,
    enabled = enabled,
    searchFilter = searchFilter,
    modifier = modifier,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    textStyle = textStyle,
    interactionSource = interactionSource,
    shape = shape,
    colors = colors,
    contentPadding = contentPadding,
)
