package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects

@Composable
fun MobEffectInstanceEditorDialog(
    value: MobEffectInstance,
    onValueChange: (MobEffectInstance) -> Unit,
    key: Identifier,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var editing by remember { mutableStateOf(value, policy = referentialEqualityPolicy()) }
    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(editing)
            true
        },
        title = title,
        content = {
            MobEffectInstanceEditorContent(editing, { editing = it }, key)
        }
    )
}

@Composable
fun MobEffectInstanceEditorContent(
    value: MobEffectInstance,
    onValueChange: (MobEffectInstance) -> Unit,
    key: Identifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MobEffectSelector(
            value.effect.value(),
            { onValueChange(value.copy(effect = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(it))) },
            label = { Text(key, suffix = "effect") },
            modifier = Modifier.fillMaxWidth()
        )
        IntField(
            value.duration,
            onValueChange = { onValueChange(value.copy(duration = it)) },
            label = { Text(key, suffix = "duration") },
            modifier = Modifier.fillMaxWidth()
        )
        IntField(
            value.amplifier,
            onValueChange = { onValueChange(value.copy(amplifier = it)) },
            label = { Text(key, suffix = "amplifier") },
            range = 0..255,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                key,
                suffix = "ambient",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, false)
            )
            Switch(value.ambient, { onValueChange(value.copy(ambient = it)) })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                key,
                suffix = "visible",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, false)
            )
            Switch(value.visible, { onValueChange(value.copy(visible = it)) })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                key,
                suffix = "show_icon",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, false)
            )
            Switch(value.showIcon, { onValueChange(value.copy(showIcon = it)) })
        }
        OutlinedLabelBox(
            label = { Text(key, suffix = "hidden_effect") },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp, 8.dp, 8.dp, 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var showDialog by remember { mutableStateOf(false) }

                value.hiddenEffect?.let { hiddenEffect ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            hiddenEffect.effect.value().displayName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.mobEffectInstanceTip(hiddenEffect, key)
                        )
                        RemoveConfirmButton(
                            key.asTranslateText(suffix = "hidden_effect", fallback = "Hidden Effect").plainText,
                            { onValueChange(value.copy(hiddenEffect = null)) }
                        )
                    }
                } ?: Text(HSLang.Common.unset, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))

                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.EditNote, null)
                }

                if (showDialog) {
                    MobEffectInstanceEditorDialog(
                        value.hiddenEffect ?: MobEffectInstance(MobEffects.LUCK),
                        {
                            onValueChange(value.copy(hiddenEffect = it))
                        },
                        key = key,
                        onDismissRequest = { showDialog = false },
                        title = { Text(key, suffix = "hidden_effect") },
                    )
                }
            }
        }
    }
}

@Composable
internal fun Modifier.mobEffectInstanceTip(mobEffectInstance: MobEffectInstance, key: Identifier): Modifier = plainTooltip {
    Column(
        modifier = Modifier.width(320.dp),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(key, suffix = "effect")
            Text(mobEffectInstance.effect.value().displayName)
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(key, suffix = "duration")
            Text(mobEffectInstance.duration.toString())
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(key, suffix = "amplifier")
            Text(mobEffectInstance.amplifier.toString())
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(key, suffix = "ambient")
            Text(mobEffectInstance.ambient.toString())
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(key, suffix = "visible")
            Text(mobEffectInstance.visible.toString())
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(key, suffix = "show_icon")
            Text(mobEffectInstance.showIcon.toString())
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(key, suffix = "hidden_effect")
            Text(mobEffectInstance.hiddenEffect?.effect?.value()?.displayName ?: HSLang.Common.unset)
        }
    }
}

fun MobEffectInstance.copy(
    effect: Holder<MobEffect> = this.effect,
    duration: Int = this.duration,
    amplifier: Int = this.amplifier,
    ambient: Boolean = this.ambient,
    visible: Boolean = this.visible,
    showIcon: Boolean = this.showIcon,
    hiddenEffect: MobEffectInstance? = this.hiddenEffect,
) = MobEffectInstance(
    effect,
    duration,
    amplifier,
    ambient,
    visible,
    showIcon,
    hiddenEffect,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobEffectSelector(
    selected: MobEffect,
    onSelect: (MobEffect) -> Unit,
    items: List<MobEffect> = BuiltInRegistries.MOB_EFFECT.toList(),
    itemEquals: (MobEffect, MobEffect) -> Boolean = { a, b -> a == b },
    content: @Composable (MobEffect) -> Unit = {
        Text(
            it.displayName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.plainTooltip { Text(it.descriptionId) }
        )
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (MobEffect, Boolean) -> Unit = { item, _ ->
        Text(
            item.displayName,
            modifier = Modifier.plainTooltip { Text(item.descriptionId) }
        )
    },
    enabled: Boolean = true,
    searchFilter: ((String, MobEffect) -> Boolean)? = { str, effect ->
        effect.descriptionId.contains(str, ignoreCase = true)
                || effect.displayName.string.contains(str, ignoreCase = true)
                || BuiltInRegistries.MOB_EFFECT.getKey(effect).toString().contains(str, ignoreCase = true)
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (MobEffect) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (MobEffect) -> Unit)?)? = null,
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