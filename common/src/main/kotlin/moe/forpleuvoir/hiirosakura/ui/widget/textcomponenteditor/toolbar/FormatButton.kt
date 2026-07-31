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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.StyleProperty
import moe.forpleuvoir.ibukigourd.input.MouseButton
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip

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
        is StyleProperty.Set<*>                 -> MaterialTheme.colorScheme.primary
    }

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
                if (effectiveState != StyleProperty.Unset) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier
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
