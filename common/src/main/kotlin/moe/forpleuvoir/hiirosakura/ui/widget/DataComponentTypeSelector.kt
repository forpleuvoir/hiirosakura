package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.selector.SelectorExpandStyle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries

@Composable
fun DataComponentTypeSelector(
    selected: DataComponentType<*>,
    onSelect: (DataComponentType<*>) -> Unit,
    items: List<DataComponentType<*>> = BuiltInRegistries.DATA_COMPONENT_TYPE.toList(),
    itemEquals: (DataComponentType<*>, DataComponentType<*>) -> Boolean = { a, b -> a == b },
    content: @Composable (DataComponentType<*>) -> Unit = {
        Text(it.keyOrUnknown.toString())
    },
    itemContent: @Composable (DataComponentType<*>, Boolean) -> Unit = { item, _ ->
        Text(item.keyOrUnknown.toString())
    },
    enabled: Boolean = true,
    searchFilter: ((DataComponentType<*>, String) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (DataComponentType<*>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (DataComponentType<*>) -> Unit)?)? = null,
    expandStyle: SelectorExpandStyle = SelectorExpandStyle.Auto(),
    onExpandedChange: ((Boolean) -> Unit)? = null,
) = Selector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    content = content,
    modifier = modifier,
    enabled = enabled,
    itemEquals = itemEquals,
    itemContent = itemContent,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    searchFilter = searchFilter,
    expandStyle = expandStyle,
    onExpandedChange = onExpandedChange
)