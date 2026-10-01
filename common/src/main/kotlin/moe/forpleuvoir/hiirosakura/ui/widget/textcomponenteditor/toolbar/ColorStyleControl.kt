package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.StyleProperty
import moe.forpleuvoir.ibukigourd.input.MouseButton
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.colorpicker.ColorPicker
import moe.forpleuvoir.ibukigourd.ui.colorpicker.LocalColorPickerEnableAlpha
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.ProvideContentColorTextStyle
import moe.forpleuvoir.ibukigourd.util.toComposeColor
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalContentColor
import moe.forpleuvoir.hiirosakura.ui.compat.closeScreen
import moe.forpleuvoir.hiirosakura.ui.compat.openComposePopupScreen
import moe.forpleuvoir.ibukigourd.util.toNebulaColor

private val MINECRAFT_CHAT_COLOR_RGB = listOf(
    0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
    0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
    0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
    0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF,
)

private val PRESET_COLORS: List<NebulaColor> = MINECRAFT_CHAT_COLOR_RGB.map { NebulaColor.fromRGB(it) }


/**
 * 工具栏颜色控件：外观与左键交互交给 [IconButton]，选中态用容器色板表达。
 *
 * 三态：`Set` 取选中配色、`null`（选区内的取值不一致）取默认底色配主色内容、其余为未选中。
 *
 * 中键打开取色器、右键清空该颜色：Initial 趟指针观察只消费次级键与三级键，主键仍留给按钮自身的 clickable。
 */
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
    val scheme = SokitsuTheme.colorScheme

    val current = (currentState as? StyleProperty.Set<NebulaColor>)?.value?.toComposeColor() ?: LocalContentColor.current

    var otherButton by remember { mutableStateOf<MouseButton?>(null) }

    IconButton(
        onClick = onLeftClick,
        modifier = modifier
            .tooltip { tip() }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        when (event.type) {
                            PointerEventType.Press   -> {
                                otherButton = when {
                                    event.buttons.isTertiaryPressed -> MouseButton.MIDDLE
                                    event.buttons.isSecondaryPressed -> MouseButton.RIGHT
                                    else -> null
                                }
                                if (otherButton != null) event.changes.forEach { it.consume() }
                            }

                            PointerEventType.Release -> {
                                val button = otherButton
                                otherButton = null
                                when (button) {
                                    MouseButton.MIDDLE -> {
                                        openColorPickerScreen(pendingColor, onPendingColorChange, enabledAlpha, tip)
                                        event.changes.forEach { it.consume() }
                                    }

                                    MouseButton.RIGHT  -> {
                                        onRightClick()
                                        event.changes.forEach { it.consume() }
                                    }

                                    else               -> {}
                                }
                            }

                            else                     -> {}
                        }
                    }
                }
            },
        contentPadding = ConfigControlDefaults.IconButtonPadding,
        colors = when (currentState) {
            is StyleProperty.Set<*> -> FlatButtonDefaults.colors(
                color = scheme.primaryContainer,
                contentColor = scheme.onPrimaryContainer,
            )

            null                    -> FlatButtonDefaults.colors(contentColor = scheme.primary)
            else                    -> FlatButtonDefaults.colors()
        },
    ) {
        label(current, pendingColor.toComposeColor())
    }
}


private fun openColorPickerScreen(
    pendingColor: NebulaColor,
    onPendingColorChange: (NebulaColor) -> Unit,
    enabledAlpha: Boolean = false,
    tip: @Composable () -> Unit
) {
    openComposePopupScreen {
        SokitsuTheme {
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

                Surface(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { },

                    color = SokitsuTheme.colorScheme.surface,
                ) {
                    Column(
                        modifier = Modifier.padding(PaddingValues(24.dp)),
                    ) {
                        Box(Modifier.padding(bottom = 12.dp)) {
                            ProvideContentColorTextStyle(
                                contentColor = SokitsuTheme.colorScheme.onSurface,
                                textStyle = SokitsuTheme.typography.title
                            ) {
                                tip()
                            }
                        }

                        CompositionLocalProvider(
                            LocalContentColor provides SokitsuTheme.colorScheme.onSurfaceVariant,
                            LocalColorPickerEnableAlpha provides enabledAlpha,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f, false)
                            ) {
                                ColorPicker(
                                    editingColor.toComposeColor(),
                                    {
                                        editingColor = it.toNebulaColor()
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
                                    Text(component = IGLang.Misc.cancel)
                                }
                            }
                            TextButton(onClick = {
                                onPendingColorChange(editingColor)
                                closeScreen()
                            }) {
                                Text(component = IGLang.Misc.confirm)
                            }
                        }
                    }
                }

            }
        }
    }
}