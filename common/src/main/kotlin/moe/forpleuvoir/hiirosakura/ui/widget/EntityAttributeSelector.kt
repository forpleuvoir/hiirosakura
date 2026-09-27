package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.Attribute
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import androidx.compose.foundation.layout.fillMaxWidth
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import androidx.compose.ui.graphics.RectangleShape

val REGISTERED_ATTRIBUTE get() = registryAccess?.lookupOrThrow(Registries.ATTRIBUTE)?.asHolderIdMap()?.toList() ?: emptyList()

@Composable
fun EntityAttributeSelector(
    selected: Holder<Attribute>,
    onSelect: (Holder<Attribute>) -> Unit,
    items: List<Holder<Attribute>> = REGISTERED_ATTRIBUTE,
    itemEquals: (Holder<Attribute>, Holder<Attribute>) -> Boolean = { a, b -> a == b },
    content: @Composable (Holder<Attribute>) -> Unit = {
        Text(it.registeredName)
    },
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (Holder<Attribute>, Boolean) -> Unit = { item, _ ->
        Text(item.registeredName)
    },
    enabled: Boolean = true,
    searchFilter: ((String, Holder<Attribute>) -> Boolean)? = { str, entry ->
        entry.registeredName.contains(str)
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (Holder<Attribute>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (Holder<Attribute>) -> Unit)?)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding(),
) {
        LabelBox(label = label, modifier = modifier, contentPadding = contentPadding) {
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
