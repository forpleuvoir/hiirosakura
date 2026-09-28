package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.selector.SelectorExpandStyle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment

object EnchatmentHelper {

    val REGISTERED_ENCHANTMENT
        get() = registryAccess?.lookupOrThrow(Registries.ENCHANTMENT)?.listElements()?.toList() ?: CLIENT_REGISTERED_ENCHANTMENT

    val REGISTERED_ENCHANTMENT_IDS get() = REGISTERED_ENCHANTMENT.map { it.registeredName }

    fun enchantmentDescription(id: String): Component {
        return REGISTERED_ENCHANTMENT.find { it.registeredName == id }?.value()?.description ?: Literal(id)
    }

    fun getEnchantmentsByItemStack(item: ItemStack): List<Holder<Enchantment>> =
        item.enchantments.enchantments.keys.toList()

    fun enchantmentDescription(enchantment: Holder<Enchantment>): Component {
        return REGISTERED_ENCHANTMENT.find { it.registeredName == enchantment.registeredName }?.value()?.description ?: Literal(enchantment.registeredName)
    }


    val CLIENT_REGISTERED_ENCHANTMENT: List<Holder.Reference<Enchantment>> by lazy {
        VanillaRegistries.createLookup()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .listElements()
            .toList()
    }

}


//@Composable
//fun EnchatmentSelector(
//    selected: Holder<Enchantment>,
//    onSelect: (Holder<Enchantment>) -> Unit,
//    items: List<Holder<Enchantment>> = EnchatmentHelper.REGISTERED_ENCHANTMENT,
//    itemEquals: (Holder<Enchantment>, Holder<Enchantment>) -> Boolean = { a, b -> a == b },
//    content: @Composable (Holder<Enchantment>) -> Unit = {
//        Text(EnchatmentHelper.enchantmentDescription(it), maxLines = 1, overflow = TextOverflow.Ellipsis)
//    },
//    label: @Composable (() -> Unit)? = null,
//    itemContent: @Composable (Holder<Enchantment>, Boolean) -> Unit = { item, _ ->
//        Text(EnchatmentHelper.enchantmentDescription(item))
//    },
//    enabled: Boolean = true,
//    searchFilter: ((String, Holder<Enchantment>) -> Boolean)? = null,
//    modifier: Modifier = Modifier,
//    itemLeadingIcon: ((Boolean) -> (@Composable (Holder<Enchantment>) -> Unit)?)? = null,
//    itemTrailingIcon: ((Boolean) -> (@Composable (Holder<Enchantment>) -> Unit)?)? = null,
//    textStyle: TextStyle = LocalTextStyle.current,
//    interactionSource: MutableInteractionSource? = null,
//    shape: Shape = RectangleShape,
//    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding(),
//) {
//    LabelBox(label = label, modifier = modifier, contentPadding = contentPadding) {
//        Selector(
//            selected = selected,
//            onSelect = onSelect,
//            items = items,
//            itemEquals = itemEquals,
//            content = content,
//            itemContent = itemContent,
//            enabled = enabled,
//            itemLeadingIcon = itemLeadingIcon,
//            itemTrailingIcon = itemTrailingIcon,
//            searchFilter = searchFilter?.let { filter -> { item, query -> filter(query, item) } },
//            modifier = Modifier.fillMaxWidth(),
//        )
//    }
//}

@Composable
fun EnchatmentSelector(
    selected: Holder<Enchantment>,
    onSelect: (Holder<Enchantment>) -> Unit,
    items: List<Holder<Enchantment>> = EnchatmentHelper.REGISTERED_ENCHANTMENT,
    itemEquals: (Holder<Enchantment>, Holder<Enchantment>) -> Boolean = { a, b -> a == b },
    content: @Composable (Holder<Enchantment>) -> Unit = {
        Text(EnchatmentHelper.enchantmentDescription(it), maxLines = 1, overflow = TextOverflow.Ellipsis)
    },
    itemContent: @Composable (Holder<Enchantment>, Boolean) -> Unit = { item, _ ->
        Text(EnchatmentHelper.enchantmentDescription(item))
    },
    enabled: Boolean = true,
    searchFilter: ((Holder<Enchantment>, String) -> Boolean)? = if (items.size > 10) {
        { e, s -> s in e.registeredName || s in EnchatmentHelper.enchantmentDescription(e).plainText }
    } else null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (Holder<Enchantment>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (Holder<Enchantment>) -> Unit)?)? = null,
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