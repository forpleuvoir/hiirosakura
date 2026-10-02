package moe.forpleuvoir.hiirosakura.ui.modifier

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import moe.forpleuvoir.compose_minecraft.platform.ui.draw.postVanillaDraw
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.FormattedCharSequence

fun Modifier.vanillaTooltip(text: Component, style: Identifier? = null): Modifier = vanillaTooltip(listOf(text), style)

@JvmName("vanillaTooltipComponents")
fun Modifier.vanillaTooltip(lines: List<Component>, style: Identifier? = null): Modifier =
    vanillaTooltip(lines.map { it.visualOrderText }, style)

@JvmName("vanillaTooltipWithFormattedCharSequence")
fun Modifier.vanillaTooltip(lines: List<FormattedCharSequence>, style: Identifier? = null): Modifier =
    vanillaTooltipLines(lines, style)

/**
 * 悬停时按原版渲染路径绘制 tooltip。
 *
 * 经 compose-minecraft 的 [postVanillaDraw]（`guiScaleEnabled = true`）进入原版通道，在回调里调
 * `VanillaDrawScope.tooltip`：该调用立即提取原版 tooltip，产物在帧末注入 Compose 层尾部，
 * 因此画在所有 Compose 内容之上。
 *
 * 坐标按 GUI 单位（窗口像素除以 guiScale）计算，与 `VanillaDrawScope` 的约定一致。
 *
 * @param lines 按视觉顺序排好的文本行
 * @param style tooltip 样式（背景与边框 sprite 的命名空间）；null 表示原版默认样式
 */
private fun Modifier.vanillaTooltipLines(lines: List<FormattedCharSequence>, style: Identifier?): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }

    val hovered by interactionSource.collectIsHoveredAsState()

    val drawModifier = if (hovered) {
        postVanillaDraw(guiScaleEnabled = true) {
            val guiScale = mc.window.guiScale.toFloat()
            val mouseX = (mc.mouseHandler.xpos() / guiScale).toInt()
            val mouseY = (mc.mouseHandler.ypos() / guiScale).toInt()

            tooltip(mc.font, lines, mouseX, mouseY, style)
        }
    } else {
        Modifier
    }

    drawModifier.hoverable(interactionSource)
}
