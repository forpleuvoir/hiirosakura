package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.icon.Play
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.selector.SelectorDialogExpanded
import moe.forpleuvoir.ibukigourd.ui.selector.SelectorExpandedDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.SharedConstants
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.sounds.SoundEvent
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * 试听按钮：按 [DisplayFieldDefaults] 的规格绘制，与框内编辑按钮同形。
 *
 * 只放音效本身，不播 UI 点击音（按下即出声，再叠一层点击音只会盖住试听）。
 */
@Composable
fun SoundPlayButton(
    soundSupplier: () -> SimpleSoundInstance,
    modifier: Modifier,
) = CompositionLocalProvider(FlatButtonDefaults.LocalPressSound provides null) {
    IconButton(
        onClick = { mc.soundManager.play(soundSupplier()) },
        modifier = modifier,
    ) {
        Icon(Icons.Play)
    }
}

@Composable
fun SoundPlayButton(
    soundEvent: SoundEvent,
    modifier: Modifier = Modifier,
) = SoundPlayButton({
    SimpleSoundInstance.forUI(soundEvent, 1f, 1.35f)
}, modifier)

@Composable
fun SoundPlayButton(
    soundEvent: Holder<SoundEvent>,
    modifier: Modifier = Modifier,
) = SoundPlayButton(soundEvent.value(), modifier)

fun SoundEvent.getSubtitle(): MutableText {
    val fallback = location.toString()
    if (SharedConstants.DEBUG_SUBTITLES) {
        return Translatable(location.path, fallback)
    }

    val weighed = mc.soundManager.getSoundEvent(location)
    if (weighed != null) {
        val subtitle = weighed.subtitle
        if (subtitle != null) {
            val contents = subtitle.contents
            if (contents is TranslatableContents) {
                return Translatable(contents.key, fallback)
            }
        }
    }
    return Translatable(fallback, fallback)
}

@Composable
fun HolderSoundEventSelector(
    value: Holder<SoundEvent>,
    onValueChange: (Holder<SoundEvent>) -> Unit,
    modifier: Modifier = Modifier,
) = SoundEventSelector(
    value.value(),
    { newValue ->
        onValueChange(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(newValue))
    },
    modifier,
)

@Composable
fun SoundEventSelector(
    value: SoundEvent,
    onValueChange: (SoundEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth().tooltip {
            Text(value.location.toString())
        },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f, false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SoundPlayButton(value)
            Spacer(Modifier.width(4.dp))
            Text(value.getSubtitle(), overflow = TextOverflow.Ellipsis, maxLines = 1)
        }
        IconButton({
            showDialog = true
        }) {
            Icon(Icons.Edit)
        }
    }

    if (showDialog) {
        SoundEventEditorDialog(
            selected = value,
            onSelect = onValueChange,
            onDismissRequest = { showDialog = false },
        )
    }
}

/**
 * 音效选择器（框内编辑按钮）：框内左端是试听按钮、右端是编辑按钮，与匹配器字段同形。
 *
 * @param value 当前音效
 * @param onValueChange 音效变化回调
 * @param modifier 作用于展示框
 */
@Composable
fun HolderSoundEventSelectorInnerEditor(
    value: Holder<SoundEvent>,
    onValueChange: (Holder<SoundEvent>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    InlineEditField(
        onEdit = { showDialog = true },
        modifier = modifier,
        tooltip = { Text(value.value().location.toString()) },
        leadingIcon = { SoundPlayButton(value) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(value.value().getSubtitle(), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
    if (showDialog) {
        SoundEventEditorDialog(
            selected = value.value(),
            onSelect = { onValueChange(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(it)) },
            onDismissRequest = { showDialog = false },
        )
    }
}

@Composable
fun SoundEventSelectorInnerEditor(
    value: SoundEvent,
    onValueChange: (SoundEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    InlineEditField(
        onEdit = { showDialog = true },
        modifier = modifier,
        tooltip = { Text(value.location.toString()) },
        leadingIcon = { SoundPlayButton(value) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(value.getSubtitle(), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
    if (showDialog) {
        SoundEventEditorDialog(
            selected = value,
            onSelect = onValueChange,
            onDismissRequest = { showDialog = false },
        )
    }
}

/**
 * 音效选择浮层：IG 选择器的弹窗载体（搜索栏 + 选项列表 + 细滚动条），选项左侧是试听按钮。
 *
 * 配置里存的是按 `Identifier` 新建的音效实例，与注册表实例不是同一个对象，因此选中态按
 * `location` 比对。
 *
 * @param selected 当前音效，null 表示未选
 * @param onSelect 选中回调
 * @param onDismissRequest 关闭浮层
 * @param items 候选音效，缺省取注册表里的全部音效
 */
@Composable
fun SoundEventEditorDialog(
    selected: SoundEvent?,
    onSelect: (SoundEvent) -> Unit,
    onDismissRequest: () -> Unit,
    items: List<SoundEvent> = BuiltInRegistries.SOUND_EVENT.toList(),
) {
    SelectorDialogExpanded(
        onDismissRequest = onDismissRequest,
        items = items,
        onToggle = { sound ->
            onSelect(sound)
            onDismissRequest()
        },
        isSelected = { sound -> sound.location == selected?.location },
        modifier = Modifier,
        itemContent = { sound, _ ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(ConfigRowWrapper.spacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SoundPlayButton(sound)
                Text(sound.getSubtitle(), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        itemLeadingIcon = null,
        itemTrailingIcon = null,
        searchFilter = { sound, query ->
            query.isBlank() ||
                    sound.location.path.contains(query, ignoreCase = true) ||
                    sound.location.toString().contains(query, ignoreCase = true) ||
                    sound.getSubtitle().plainText.contains(query, ignoreCase = true)
        },
        onCancel = null,
        title = null,
        minWidth = SelectorExpandedDefaults.dialogMinWidth,
        maxWidth = SelectorExpandedDefaults.dialogMaxWidth,
        listMaxHeight = SelectorExpandedDefaults.dialogListMaxHeight,
    )
}

@Composable
fun OptionalHolderSoundEventSelector(
    value: Optional<Holder<SoundEvent>>,
    onValueChange: (Optional<Holder<SoundEvent>>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth().thenIf(value.isPresent) {
            Modifier.tooltip {
                Text(value.get().value().location.toString())
            }
        },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f, false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            value.getOrNull()?.let {
                SoundPlayButton(it)
                Spacer(Modifier.width(4.dp))
                Text(it.value().getSubtitle(), overflow = TextOverflow.Ellipsis, maxLines = 1)
            } ?: run {
                Spacer(Modifier.width(4.dp))
                Text(component = HSLang.Common.unset)
            }
        }

        Row {
            value.getOrNull()?.let {
                IconButton({ onValueChange(Optional.empty()) }) {
                    Icon(Icons.Delete)
                }
            }
            IconButton({
                showDialog = true
            }) {
                Icon(Icons.Edit)
            }
        }
    }

    if (showDialog) {
        SoundEventEditorDialog(
            selected = value.getOrNull()?.value(),
            onSelect = { sound -> onValueChange(Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound))) },
            onDismissRequest = { showDialog = false },
        )
    }
}

