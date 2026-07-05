package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.preset.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import net.minecraft.world.item.Rarity
import kotlin.enums.enumEntries


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RaritySelector(
    selected: Rarity,
    onSelect: (Rarity) -> Unit,
    items: List<Rarity> = enumEntries<Rarity>(),
    itemEquals: (Rarity, Rarity) -> Boolean = { a, b -> a == b },
    content: @Composable (Rarity) -> Unit = {
        Text(it.translateText.withStyle(it.color()))
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (Rarity, Boolean) -> Unit = { item, _ ->
        Text(item.translateText.withStyle(item.color()))
    },
    enabled: Boolean = true,
    enabledSearch: Boolean = false,
    searchFilter: ((String, Rarity) -> Boolean) = { _, _ -> true },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (Rarity) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (Rarity) -> Unit)?)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
) = EnumSelector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    itemEquals = itemEquals,
    content = content,
    labelPosition = labelPosition,
    label = label,
    itemContent = itemContent,
    enabled = enabled,
    enabledSearch = enabledSearch,
    searchFilter = searchFilter,
    modifier = modifier,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    textStyle = textStyle,
    interactionSource = interactionSource,
    shape = shape,
    colors = colors,
    contentPadding = contentPadding
)
