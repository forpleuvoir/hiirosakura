package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.toolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.StyleProperty
import moe.forpleuvoir.ibukigourd.input.MouseButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button

@Composable
fun FormatButton(
    state: StyleProperty<Boolean>?,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    tip: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pressedButton by remember { mutableStateOf<MouseButton?>(null) }


    val effectiveState: StyleProperty<Boolean> = state ?: StyleProperty.Unset

    val color = when (effectiveState) {
        StyleProperty.None, StyleProperty.Unset -> LocalContentColor.current
        is StyleProperty.Set<*>                 -> SokitsuTheme.colorScheme.primary
    }


    val interactionSource = remember { MutableInteractionSource() }

    val isHovered by interactionSource.collectIsHoveredAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) SokitsuTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else SokitsuTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        animationSpec = tween(200),
        label = "bgHoverHighlight"
    )

    Box(
        modifier = modifier
            .size(36.dp)
            .hoverable(interactionSource)
            .background(backgroundColor, CircleShape)
            .thenIf(effectiveState != StyleProperty.Unset) { Modifier.border(2.dp, SokitsuTheme.colorScheme.primary, CircleShape) }
            .tooltip { tip() }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        when (event.type) {
                            PointerEventType.Press   -> {
                                pressedButton = when {
                                    event.buttons.isPrimaryPressed   -> MouseButton.LEFT
                                    event.buttons.isSecondaryPressed -> MouseButton.RIGHT
                                    else                             -> null
                                }
                                event.changes.forEach { it.consume() }
                            }

                            PointerEventType.Release -> {
                                val button = pressedButton
                                pressedButton = null
                                when (button) {
                                    MouseButton.LEFT  -> onLeftClick()
                                    MouseButton.RIGHT -> onRightClick()
                                    else              -> {}
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
        CompositionLocalProvider(LocalContentColor provides color) {
            label()
        }
    }
}
