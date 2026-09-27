package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.SharedConstants
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.sounds.SoundEvent
import java.util.*
import kotlin.jvm.optionals.getOrNull
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.hiirosakura.ui.icon.HSIcons
import moe.forpleuvoir.hiirosakura.ui.icon.VectorIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.HorizontalDivider
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import androidx.compose.ui.graphics.RectangleShape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.PlayArrow
import androidx.compose.foundation.clickable

@Composable
fun SoundPlayButton(
    soundSupplier: () -> SimpleSoundInstance,
    modifier: Modifier,
) = IconButton({
    mc.soundManager.play(soundSupplier())
}, modifier = modifier) {
    VectorIcon(HSIcons.PlayArrow, contentDescription = "PlaySound")
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
fun SoundEventWrapper(
    soundEvent: SoundEvent,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.tooltip {
            Text(soundEvent.location.toString())
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoundPlayButton(soundEvent)
        Spacer(Modifier.width(8.dp))
        Text(soundEvent.getSubtitle(), overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun HolderSoundEventSelector(
    value: Holder<SoundEvent>,
    onValueChange: (Holder<SoundEvent>) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
    labelStartPadding: Dp? = 16.dp,
) = SoundEventSelector(
    value.value(),
    { newValue ->
        onValueChange(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(newValue))
    },
    modifier,
    label,
    shape,
    contentPadding,
    labelStartPadding,
)

@Composable
fun SoundEventSelector(
    value: SoundEvent,
    onValueChange: (SoundEvent) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
    labelStartPadding: Dp? = 16.dp,
) {
    var showDialog by remember { mutableStateOf(false) }
    LabelBox(
        label,
        modifier.tooltip {
            Text(value.location.toString())
        },
        
        contentPadding = contentPadding,
        labelStartPadding = labelStartPadding,
    ) {
        Row(
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
    }

    if (showDialog) {
        SimpleAlertDialog(
            onDismissRequest = { showDialog = false },
            onConfirmRequest = { true },
            content = {
                SoundEventBrowser(modifier = Modifier.height(520.dp).fillMaxWidth()) {
                    onValueChange(it)
                    showDialog = false
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun OptionalHolderSoundEventSelector(
    value: Optional<Holder<SoundEvent>>,
    onValueChange: (Optional<Holder<SoundEvent>>) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(top = 4.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
    labelStartPadding: Dp? = 16.dp,
) {
    var showDialog by remember { mutableStateOf(false) }

    LabelBox(
        label,
        modifier.thenIf(value.isPresent) {
            Modifier.tooltip {
                Text(value.get().value().location.toString())
            }
        },
        
        contentPadding = contentPadding,
        labelStartPadding = labelStartPadding,
    ) {
        Row(
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
    }

    if (showDialog) {
        SimpleAlertDialog(
            onDismissRequest = { showDialog = false },
            onConfirmRequest = { true },
            content = {
                SoundEventBrowser(modifier = Modifier.height(520.dp).fillMaxWidth()) {
                    onValueChange(Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(it)))
                    showDialog = false
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SoundEventBrowser(
    items: List<SoundEvent> = BuiltInRegistries.SOUND_EVENT.toList(),
    modifier: Modifier = Modifier,
    onClick: (SoundEvent) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        val searchQuery = rememberTextFieldState()

        val displayItems by remember(items, searchQuery.text) {
            derivedStateOf {
                if (searchQuery.text.isBlank()) items
                else items.filter {
                    it.location.path.contains(searchQuery.text, ignoreCase = true) ||
                            it.location.toString().contains(searchQuery.text, ignoreCase = true) ||
                            it.getSubtitle().plainText.contains(searchQuery.text, ignoreCase = true)
                }
            }
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .height(40.dp)
        ) {
            Icon(Icons.Search)
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                searchQuery,
                modifier = Modifier.weight(1f),
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(SokitsuTheme.colorScheme.primary),
            )
            Spacer(Modifier.width(8.dp))
            if (searchQuery.text.isNotEmpty()) {
                val interactionSource = remember { MutableInteractionSource() }

                val isHovered by interactionSource.collectIsHoveredAsState()

                Icon(
                    Icons.Close,
                    modifier = Modifier
                        .clickable { searchQuery.clearText() }
                        .hoverable(interactionSource)
                        .background(
                            color = if (isHovered) SokitsuTheme.colorScheme.surfaceVariant else Color.Transparent)
                        .padding(6.dp))
            }
        }

        HorizontalDivider()

        Box(modifier = Modifier.fillMaxSize()) {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(top = 4.dp),
            ) {
                items(displayItems) { soundEvent ->

                    val interactionSource = remember { MutableInteractionSource() }

                    val isHovered by interactionSource.collectIsHoveredAsState()
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isHovered) SokitsuTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        else SokitsuTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        animationSpec = tween(200),
                    )

                    SoundEventWrapper(
                        soundEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .hoverable(interactionSource)
                            .background(backgroundColor, RectangleShape)
                            .clickable { onClick(soundEvent) }
                    )
                }
            }

            VerticalScroller(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollerAdapter(listState)
            )
        }
    }
}