package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.toolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.StyleProperty
import moe.forpleuvoir.ibukigourd.input.MouseButton
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.closeScreen
import moe.forpleuvoir.ibukigourd.ui.openComposePopupScreen
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IGCompositionLocalProvider
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme
import moe.forpleuvoir.ibukigourd.ui.preset.ColorPicker
import moe.forpleuvoir.ibukigourd.ui.preset.LocalColorPickerEnableAlpha
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.util.ProvideContentColorTextStyle
import moe.forpleuvoir.ibukigourd.ui.util.toComposeColor
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor

private val MINECRAFT_CHAT_COLOR_RGB = listOf(
    0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
    0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
    0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
    0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF,
)

private val PRESET_COLORS: List<NebulaColor> = MINECRAFT_CHAT_COLOR_RGB.map { NebulaColor.fromRGB(it) }


@Composable
fun ColorStyleControl(
    currentState: StyleProperty<NebulaColor>?,
    pendingColor: NebulaColor,
    onPendingColorChange: (NebulaColor) -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    enabledAlpha: Boolean = false,
    tip: @Composable () -> Unit,
    label: @Composable (current: Color, pending: Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val current = (currentState as? StyleProperty.Set<NebulaColor>)?.value?.toComposeColor ?: LocalContentColor.current

    var pressedButton by remember { mutableStateOf<MouseButton?>(null) }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        animationSpec = tween(200),
        label = "bgHoverHighlight"
    )
    Box(
        modifier = modifier
            .size(36.dp)
            .hoverable(interactionSource)
            .background(backgroundColor, CircleShape)
            .then(
                if (currentState != null && currentState != StyleProperty.Unset)
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier
            )
            .plainTooltip { tip() }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        when (event.type) {
                            PointerEventType.Press   -> {
                                pressedButton = when {
                                    event.buttons.isPrimaryPressed   -> MouseButton.LEFT
                                    event.buttons.isTertiaryPressed  -> MouseButton.MIDDLE
                                    event.buttons.isSecondaryPressed -> MouseButton.RIGHT
                                    else                             -> null
                                }
                                event.changes.forEach { it.consume() }
                            }

                            PointerEventType.Release -> {
                                val btn = pressedButton
                                pressedButton = null
                                when (btn) {
                                    MouseButton.LEFT   -> onLeftClick()
                                    MouseButton.MIDDLE -> {
                                        openColorPickerScreen(pendingColor, onPendingColorChange, enabledAlpha, tip)
                                    }

                                    MouseButton.RIGHT  -> onRightClick()
                                    else               -> {}
                                }
                                event.changes.forEach { it.consume() }
                            }

                            else                     -> {}
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        label(current, pendingColor.toComposeColor)
    }
}


private fun openColorPickerScreen(
    pendingColor: NebulaColor,
    onPendingColorChange: (NebulaColor) -> Unit,
    enabledAlpha: Boolean = false,
    tip: @Composable () -> Unit
) {
    openComposePopupScreen {
        IbukiGourdTheme {
            var editingColor by remember { mutableStateOf(pendingColor) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        closeScreen()
                    },
                contentAlignment = Alignment.Center
            ) {
                IGCompositionLocalProvider {
                    Surface(
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { },
                        shape = AlertDialogDefaults.shape,
                        color = AlertDialogDefaults.containerColor,
                        tonalElevation = AlertDialogDefaults.TonalElevation,
                    ) {
                        Column(
                            modifier = Modifier.padding(PaddingValues(24.dp)),
                        ) {
                            Box(Modifier.padding(bottom = 12.dp)) {
                                ProvideContentColorTextStyle(
                                    contentColor = AlertDialogDefaults.titleContentColor,
                                    textStyle = MaterialTheme.typography.headlineSmall
                                ) {
                                    tip()
                                }
                            }

                            CompositionLocalProvider(
                                LocalContentColor provides AlertDialogDefaults.textContentColor,
                                LocalColorPickerEnableAlpha provides enabledAlpha,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f, false)
                                ) {
                                    ColorPicker(
                                        editingColor,
                                        {
                                            editingColor = it
                                        },
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(PaddingValues(top = 16.dp)),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(Modifier.padding(end = 8.dp)) {
                                    TextButton(onClick = { closeScreen() }) {
                                        Text(IGLang.Misc.cancel)
                                    }
                                }
                                TextButton(onClick = {
                                    onPendingColorChange(editingColor)
                                    closeScreen()
                                }) {
                                    Text(IGLang.Misc.confirm)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}