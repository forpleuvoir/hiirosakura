package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.config.RenderInfoAddon
import moe.forpleuvoir.hiirosakura.util.getEnchantmentTextWithLvl
import moe.forpleuvoir.hiirosakura.util.tooltipType
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderText
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.IGDrawContext
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.render.math.Vector2f
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.client.font.TextRenderer
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack

object TooltipRenderAddon {

    @JvmStatic
    fun renderEnchantmentWhenSwitch(y: Int, itemStack: ItemStack, textRenderer: TextRenderer, context: IGDrawContext, alpha: Int) {
        if (!RenderInfoAddon.showEnchantmentWhenSwitch.value) return
        val texts = itemStack.getEnchantmentTextWithLvl(TooltipContext.DEFAULT, mc.tooltipType)
        if (texts.isEmpty()) return
        val spacing = 1
        val maxHeight = texts.size * (textRenderer.fontHeight + spacing)
        val maxWidth = texts.maxWidth(textRenderer)
        val box = Box(
            x = (context.scaledWindowWidth - maxWidth) / 2f,
            y = (y - maxHeight).toFloat(),
            width = maxWidth,
            height = maxHeight
        )
        context.batchRenderText(textRenderer) {
            val verticalOffsets =
                Arrangement.spacedBy(spacing.toFloat(), Alignment.CenterVertically).arrange(box.width, List(texts.size) { textRenderer.fontHeight.toFloat() })
            val horizontalOffsets = texts.map { Alignment.CenterHorizontally.align(box.width, textRenderer.getWidth(it).toFloat()) }
            horizontalOffsets.zip(verticalOffsets) { x, y ->
                Vector2f(box.x + x, box.y + y)
            }.forEachIndexed { index, offset ->
                pushText(texts[index], x = offset.x, y = offset.y, shadow = true, color = Color(texts[index].style.color?.rgb ?: 0XAAAAAA).alpha(alpha))
            }
        }

    }


}