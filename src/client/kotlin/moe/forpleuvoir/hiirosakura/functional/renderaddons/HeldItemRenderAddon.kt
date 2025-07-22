package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.vector2f
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderBox
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderText
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.IGDrawContext
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.math.Vector2f
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.float
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.item.ItemStack

object HeldItemRenderAddon : ModConfigContainer("held_item") {

    val enable by keyBindBoolean("enable", value = false)

    val offset by vector2f("offset", Vector2f(0f, 0f), Vector2f(-200f, -200f), Vector2f(200f, 200f))

    val spacing by float("spacing", 1f, 0f, 10f)

    val shadow by boolean("shadow", true)

    val textBackground by color("text_background", Colors.BLACK.alpha(0f))

    val overallBackground by color("overall_background", Colors.BLACK.alpha(0f))

    val itemStackInfo = addConfig(ItemStackInfo())

    @JvmStatic
    fun shouldRender(new: ItemStack, origin: ItemStack): Boolean {
        val new = new.copy()
        val origin = origin.copy()
        if (!itemStackInfo.count.value) {
            new.count = 1
            origin.count = 1
        }
        if (!itemStackInfo.durability.value) {
            new.damage = 1
            origin.damage = 1
        }
        return enable.value && !ItemStack.areEqual(new, origin)
    }

    @JvmStatic
    internal fun render(context: IGDrawContext, textRenderer: TextRenderer, y: Int, alpha: Int, itemStack: ItemStack) {
        val texts = itemStackInfo.getItemStackInfo(itemStack)
        if (texts.isEmpty()) return
        val maxHeight = texts.size * (textRenderer.fontHeight + spacing)
        val maxWidth = texts.maxWidth
        val box = Box(
            x = (context.scaledWindowWidth - maxWidth) / 2f,
            y = y.toFloat() - maxHeight,
            width = maxWidth,
            height = maxHeight
        )
        context.useMatrixStack {
            it.translate(offset.x(), offset.y(), 0f)
            if (overallBackground.alpha > 5) {
                batchRenderBox {
                    pushRoundBox(box.expandEdges(4f), overallBackground.opacity(alpha), 2)
                }
            }
            batchRenderText(textRenderer) {
                val verticalOffsets = Arrangement.spacedBy(spacing).arrange(box.width, List(texts.size) { textRenderer.fontHeight.toFloat() })
                val horizontalOffsets = texts.map { text -> Alignment.CenterHorizontally.align(box.width, text.width) }
                horizontalOffsets.zip(verticalOffsets) { x, y ->
                    Vector2f(box.x + x, box.y + y)
                }.forEachIndexed { index, offset ->
                    val text = texts[index]
                    pushText(
                        text,
                        offset.x,
                        offset.y,
                        shadow,
                        TextLayerType.NORMAL,
                        Color(text.style.color?.rgb ?: 0xAAAAAA).alpha(alpha),
                        if (text.string.isEmpty()) Colors.BLACK.alpha(0) else textBackground.opacity(alpha),
                        LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        textRenderer.isRightToLeft
                    )
                }
            }
        }

    }

}



