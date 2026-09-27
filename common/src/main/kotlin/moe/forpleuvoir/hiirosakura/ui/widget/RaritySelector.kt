package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import net.minecraft.world.item.Rarity
import kotlin.enums.enumEntries
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import androidx.compose.ui.graphics.RectangleShape


@Composable
fun RaritySelector(
    selected: Rarity,
    onSelect: (Rarity) -> Unit,
    items: List<Rarity> = enumEntries<Rarity>(),
    itemEquals: (Rarity, Rarity) -> Boolean = { a, b -> a == b },
    content: @Composable (Rarity) -> Unit = {
        Text(it.translateText.withStyle(it.color()))
    },
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (Rarity, Boolean) -> Unit = { item, _ ->
        Text(item.translateText.withStyle(item.color()))
    },
    enabled: Boolean = true,
    searchFilter: ((String, Rarity) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (Rarity) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (Rarity) -> Unit)?)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding(),
) = EnumSelector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    itemEquals = itemEquals,
    content = content,
    label = label,
    itemContent = itemContent,
    enabled = enabled,
    searchFilter = searchFilter,
    modifier = modifier,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    textStyle = textStyle,
    interactionSource = interactionSource,
    
    contentPadding = contentPadding
)
