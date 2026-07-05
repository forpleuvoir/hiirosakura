package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.ui.preset.Selector
import moe.forpleuvoir.ibukigourd.ui.preset.Text
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EnchatmentSelector(
    selected: Holder<Enchantment>,
    onSelect: (Holder<Enchantment>) -> Unit,
    items: List<Holder<Enchantment>> = EnchatmentHelper.REGISTERED_ENCHANTMENT,
    itemEquals: (Holder<Enchantment>, Holder<Enchantment>) -> Boolean = { a, b -> a == b },
    content: @Composable (Holder<Enchantment>) -> Unit = {
        Text(EnchatmentHelper.enchantmentDescription(it))
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (Holder<Enchantment>, Boolean) -> Unit = { item, _ ->
        Text(EnchatmentHelper.enchantmentDescription(item))
    },
    enabled: Boolean = true,
    enabledSearch: Boolean = false,
    searchFilter: ((String, Holder<Enchantment>) -> Boolean) = { _, _ -> true },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (Holder<Enchantment>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (Holder<Enchantment>) -> Unit)?)? = null,
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
