package moe.forpleuvoir.hiirosakura.functional.renderaddons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastRoundToInt
import moe.forpleuvoir.hiirosakura.util.expandEdges
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.config.item.configVector2f
import moe.forpleuvoir.ibukigourd.render.extension.pushRoundRect
import moe.forpleuvoir.ibukigourd.render.extension.pushText
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.math.Vector2f
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configBoolean
import moe.forpleuvoir.nebula.config.item.configColor
import moe.forpleuvoir.nebula.config.item.configFloat
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.item.ItemStack

object HeldItemRenderAddon : ConfigGroup("held_item") {

    val enable by configToggleKeybind("enable", false)

    val offset by configVector2f("offset", Vector2f(0f, 0f), Vector2f(-200f, -200f), Vector2f(200f, 200f))

    val spacing by configFloat("spacing", 1f, 0f, 10f)

    val shadow by configBoolean("shadow", true)

    val textBackground by configColor("text_background", Colors.TRANSPARENT)

    val overallBackground by configColor("overall_background", Colors.TRANSPARENT)

    val itemStackInfo = addConfig(ItemStackInfo(enableScript = true))

    @JvmStatic
    fun shouldRender(new: ItemStack, origin: ItemStack): Boolean {
        val new = new.copy()
        val origin = origin.copy()
        if (!itemStackInfo.count) {
            new.count = 1
            origin.count = 1
        }
        if (!itemStackInfo.damage.getValue()) {
            new.damageValue = 1
            origin.damageValue = 1
        }
        return enable.enabled && !ItemStack.matches(new, origin)
    }

    private val density = Density(1f)

    @JvmStatic
    fun render(guiGraphics: GuiGraphicsExtractor, font: Font, y: Int, alpha: Int, itemStack: ItemStack) {
        val texts = itemStackInfo.getItemStackInfo(itemStack, mc.player)
        if (texts.isEmpty()) return
        val maxHeight = texts.size * (font.lineHeight + spacing) - spacing
        val maxWidth = texts.maxWidth
        val area = Rect(
            Offset(
                x = (guiGraphics.guiWidth() - maxWidth) / 2f,
                y = y.toFloat() - maxHeight,
            ),
            Size(
                width = maxWidth,
                height = maxHeight
            )
        )
        guiGraphics.apply {
            pose().popMatrix()
            pose().translate(offset.x(), offset.y())
            if (overallBackground.alpha > 5) {
                pushRoundRect(area.expandEdges(4f), overallBackground.opacity(alpha), 2)
            }
            val verticalOffsets = IntArray(texts.size)
            Arrangement.spacedBy(spacing.dp).run {
                density.arrange(area.height.fastRoundToInt(), IntArray(texts.size) { font.lineHeight }, verticalOffsets)
            }
            val horizontalOffsets = texts.map { Alignment.CenterHorizontally.align(it.width.toInt(), area.width.fastRoundToInt(), LayoutDirection.Ltr) }

            verticalOffsets.zip(horizontalOffsets).fastForEachIndexed { idx, (y, x) ->
                val text = texts[idx]
                pushText(
                    text,
                    area.left + x.toFloat(),
                    area.top + y.toFloat(),
                    color = Color.fromRGB(text.style.color?.value ?: 0xAAAAAA).alpha(alpha),
                    backgroundColor = if (text.string.isEmpty()) Colors.TRANSPARENT else textBackground.opacity(alpha),
                    shadow = shadow
                )
            }

            pose().popMatrix()
        }
    }

}



