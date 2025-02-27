package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.util.getEnchantmentTextWithLvl
import moe.forpleuvoir.hiirosakura.util.tooltipType
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.vector2f
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderText
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.IGDrawContext
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.math.Vector2f
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack

object ShowEnchantmentWhenSwitch : ModConfigContainer("show_enchantment_when_switch") {

    val enable by keyBindBoolean("enable", value = false)

    val offset by vector2f("offset", Vector2f(0f, 0f), Vector2f(-200f, -200f), Vector2f(200f, 200f))

    @JvmStatic
    fun shouldRender(new: ItemStack, origin: ItemStack): Boolean {
        if (!enable.value || new == origin) return false
        return new.enchantments != origin.enchantments
    }

    /**
     * 渲染切换时的附魔文字。
     *
     * 此方法根据给定的物品堆栈中的附魔信息，在屏幕上指定的区域渲染附魔文本。当显示附魔信息被禁用时，方法将提前返回。
     *
     * @param y 坐标的Y值，用于确定附魔文本渲染的初始垂直位置。
     * @param itemStack 包含附魔信息的物品堆栈。
     * @param textRenderer 用于渲染文本的文本渲染器。
     * @param context 当前的绘制上下文，负责绘制的环境信息。
     * @param alpha 附魔文字的透明度，用于调整文本渲染的视觉效果。
     */
    @JvmStatic
    internal fun render(y: Int, itemStack: ItemStack, textRenderer: TextRenderer, context: IGDrawContext, alpha: Int) {
        if (!enable.value) return
        val texts = itemStack.getEnchantmentTextWithLvl(TooltipContext.DEFAULT, mc.tooltipType)
        if (texts.isEmpty()) return
        val spacing = 1
        val maxHeight = texts.size * (textRenderer.fontHeight + spacing)
        val maxWidth = texts.maxWidth
        val box = Box(
            x = (context.scaledWindowWidth - maxWidth) / 2f,
            y = (y - maxHeight).toFloat(),
            width = maxWidth,
            height = maxHeight
        )
        context.useMatrixStack {
            it.translate(offset.x(), offset.y(), 0f)
            batchRenderText(textRenderer) {
                val verticalOffsets = Arrangement.spacedBy(spacing.toFloat()).arrange(box.width, List(texts.size) { textRenderer.fontHeight.toFloat() })
                val horizontalOffsets = texts.map { Alignment.CenterHorizontally.align(box.width, it.width) }
                horizontalOffsets.zip(verticalOffsets) { x, y ->
                    Vector2f(box.x + x, box.y + y)
                }.forEachIndexed { index, offset ->
                    val text = texts[index]
                    pushText(
                        text,
                        offset.x,
                        offset.y,
                        true,
                        TextLayerType.NORMAL,
                        Color(text.style.color?.rgb ?: 0xAAAAAA).alpha(alpha),
                        Colors.BLACK.alpha(0),
                        LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        textRenderer.isRightToLeft
                    )
                }
            }
        }

    }

}



