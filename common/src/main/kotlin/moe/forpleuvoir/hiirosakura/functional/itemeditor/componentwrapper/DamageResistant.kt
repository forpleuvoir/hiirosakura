package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.ui.preset.Selector
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DeathMessageType.INTENTIONAL_GAME_DESIGN
import net.minecraft.world.item.component.DamageResistant


@Composable
fun DamageResistantComponentWrapper(
    key: Identifier,
    value: DamageResistant,
    onValueChange: (DamageResistant) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {

}

internal val damageTypeTags
    get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE).tags.map { it.key() }

internal val TagKey<DamageType>.damageTypes
    get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE).getTagOrEmpty(this)

internal val DamageType.translatableText
    get() = when (this.deathMessageType()) {
        INTENTIONAL_GAME_DESIGN -> Text.translatable(
            "death.attack.${this.msgId}.message",
            "xx",
            ComponentUtils.wrapInSquareBrackets(Text.translatable("death.attack.${this.msgId}.link"))
        )

        else                    -> Text.translatable("death.attack.${this.msgId}", "xx", "oo")
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DamageTypeTagSelector(
    selected: TagKey<DamageType>,
    onSelect: (TagKey<DamageType>) -> Unit,
    items: List<TagKey<DamageType>> = damageTypeTags.toList(),
    itemEquals: (TagKey<DamageType>, TagKey<DamageType>) -> Boolean = { a, b -> a == b },
    content: @Composable (TagKey<DamageType>) -> Unit = {
        Row(Modifier.plainTooltip {
            val types = it.damageTypes
            if (types.count() == 0) {
                Text(IGLang.Misc.hasNothing)
                return@plainTooltip
            } else {
                Column {
                    types.take(20).forEach { type ->
                        Text(type.value().translatableText)
                    }
                    if (types.count() > 20) Text("...")
                }
            }
        }) {
            Text("#${it.location}")
        }
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (TagKey<DamageType>, Boolean) -> Unit = { item, _ ->
        Row(Modifier.plainTooltip {
            val types = item.damageTypes
            if (types.count() == 0) {
                Text(IGLang.Misc.hasNothing)
                return@plainTooltip
            } else {
                Column {
                    types.take(20).forEach { type ->
                        Text(type.value().translatableText)
                    }
                    if (types.count() > 20) Text("...")
                }
            }
        }) {
            Text("#${item.location}")
        }
    },
    enabled: Boolean = true,
    searchFilter: ((String, TagKey<DamageType>) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<DamageType>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<DamageType>) -> Unit)?)? = null,
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
    contentPadding = contentPadding
)
