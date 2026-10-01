package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.toolbar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.StyleProperty
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip

/**
 * 工具栏格式按钮：外观与左键交互交给 [IconButton]，选中态用容器色板表达。
 *
 * 三态：`Set` 取选中配色、`null`（选区内的取值不一致）取默认底色配主色内容、
 * 其余（`None` / `Unset`）为未选中。
 *
 * 右键清空该属性：Initial 趟指针观察只消费次级键，主键仍留给按钮自身的 clickable。
 */
@Composable
fun FormatButton(
    state: StyleProperty<Boolean>?,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    tip: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = SokitsuTheme.colorScheme

    val effectiveState: StyleProperty<Boolean> = state ?: StyleProperty.Unset

    var rightPressed by remember { mutableStateOf(false) }

    IconButton(
        onClick = onLeftClick,
        modifier = modifier
            .tooltip { tip() }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        when (event.type) {
                            PointerEventType.Press -> if (event.buttons.isSecondaryPressed) {
                                rightPressed = true
                                event.changes.forEach { it.consume() }
                            }

                            PointerEventType.Release -> if (rightPressed) {
                                rightPressed = false
                                onRightClick()
                                event.changes.forEach { it.consume() }
                            }

                            else -> {}
                        }
                    }
                }
            },
        contentPadding = ConfigControlDefaults.IconButtonPadding,
        colors = when {
            effectiveState is StyleProperty.Set<*> -> FlatButtonDefaults.colors(
                color = scheme.primaryContainer,
                contentColor = scheme.onPrimaryContainer,
            )

            state == null                          -> FlatButtonDefaults.colors(contentColor = scheme.primary)
            else                                   -> FlatButtonDefaults.colors()
        },
    ) {
        label()
    }
}
