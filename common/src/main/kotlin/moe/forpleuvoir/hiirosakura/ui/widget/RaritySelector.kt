package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.selector.SelectorExpandStyle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import net.minecraft.world.item.Rarity
import kotlin.enums.enumEntries

@Composable
fun RaritySelector(
    selected: Rarity,
    onSelect: (Rarity) -> Unit,
    items: List<Rarity> = enumEntries<Rarity>(),
    itemEquals: (Rarity, Rarity) -> Boolean = { a, b -> a == b },
    content: @Composable (Rarity) -> Unit = {
        Text(it.translateText.withStyle(it.color()))
    },
    itemContent: @Composable (Rarity, Boolean) -> Unit = { item, _ ->
        Text(item.translateText.withStyle(item.color()))
    },
    enabled: Boolean = true,
    searchFilter: ((Rarity, String) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (Rarity) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (Rarity) -> Unit)?)? = null,
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