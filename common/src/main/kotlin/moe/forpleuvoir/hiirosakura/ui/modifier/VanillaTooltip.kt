package moe.forpleuvoir.hiirosakura.ui.modifier

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.isActive
import moe.forpleuvoir.ibukigourd.ui.skia.LocalSkiaSurface
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence

fun Modifier.vanillaTooltip(text: Component): Modifier = vanillaTooltip(text) { text, x, y ->
    setTooltipForNextFrame(mc.font, text, x, y)
}

fun Modifier.vanillaTooltip(lines: List<Component>): Modifier = vanillaTooltip(lines.map { it.visualOrderText })

@JvmName("vanillaTooltipWithFormattedCharSequence")
fun Modifier.vanillaTooltip(lines: List<FormattedCharSequence>): Modifier = vanillaTooltip(lines) { lines, x, y ->
    setTooltipForNextFrame(mc.font, lines, x, y)
}

fun <T : Any> Modifier.vanillaTooltip(text: T, block: GuiGraphicsExtractor.(T, mouseX: Int, mouseY: Int) -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val surface = LocalSkiaSurface.current

    LaunchedEffect(hovered, text, surface) {
        if (!hovered) return@LaunchedEffect

        while (isActive) {
            withFrameNanos {
                surface.postRender {
                    val guiScale = mc.window.guiScale.toFloat()
                    val mouseX = (mc.mouseHandler.xpos() / guiScale).toInt()
                    val mouseY = (mc.mouseHandler.ypos() / guiScale).toInt()

                    block(text, mouseX, mouseY)
                }
            }
        }
    }

    hoverable(interactionSource)
}