package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.vector2f
import moe.forpleuvoir.ibukigourd.gui.base.extensions.guigraphics.useMatrixStack
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.IGGuiGraphics
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.math.Vector2f
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.float
import net.minecraft.client.gui.Font
import net.minecraft.world.item.ItemStack

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
        if (!itemStackInfo.count) {
            new.count = 1
            origin.count = 1
        }
        if (!itemStackInfo.damage.getValue()) {
            new.damageValue = 1
            origin.damageValue = 1
        }
        return enable.value && !ItemStack.matches(new, origin)
    }

    @JvmStatic
    fun render(guiGraphics: IGGuiGraphics, font: Font, y: Int, alpha: Int, itemStack: ItemStack) {
        val texts = itemStackInfo.getItemStackInfo(itemStack, mc.player)
        if (texts.isEmpty()) return
        val maxHeight = texts.size * (font.lineHeight + spacing) - spacing
        val maxWidth = texts.maxWidth
        val box = Box(
            x = (guiGraphics.guiWidth() - maxWidth) / 2f,
            y = y.toFloat() - maxHeight,
            width = maxWidth,
            height = maxHeight
        )
        guiGraphics.useMatrixStack {
            it.translate(offset.x(), offset.y())
            if (overallBackground.alpha > 5) {
                pushRoundBox(box.expandEdges(4f), overallBackground.opacity(alpha), 2)
            }
            val verticalOffsets = Arrangement.spacedBy(spacing).arrange(box.width, List(texts.size) { font.lineHeight.toFloat() })
            val horizontalOffsets = texts.map { text -> Alignment.CenterHorizontally.align(box.width, text.width) }
            horizontalOffsets.zip(verticalOffsets) { x, y ->
                Vector2f(box.x + x, box.y + y)
            }.forEachIndexed { index, offset ->
                val text = texts[index]
                pushText(
                    text,
                    offset.x,
                    offset.y,
                    color = Color(text.style.color?.value ?: 0xAAAAAA).alpha(alpha.coerceIn(0, 255)),
                    if (text.string.isEmpty()) Colors.BLACK.alpha(0) else textBackground.opacity(alpha.coerceIn(0, 255)),
                    shadow = shadow
                )
            }
        }

    }

}



