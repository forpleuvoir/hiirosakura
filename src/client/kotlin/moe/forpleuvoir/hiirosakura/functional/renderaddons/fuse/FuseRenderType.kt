package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderTextureColored
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.ibukigourd.render.disableDepthTest
import moe.forpleuvoir.ibukigourd.render.enableDepthTest
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.client.util.math.MatrixStack

enum class FuseRenderType {
    Text, Box, None;

    companion object {

        private val TEXTURE = TextureInfo(16, 16, identifier("texture/gui/fuse.png"))

        private val BORDER = WidgetTexture(Corner(1), 0, 0, 16, 8, TEXTURE)

        private val CONTENT = WidgetTexture(Corner(1), 0, 8, 16, 16, TEXTURE)

        fun renderBox(matrixStack: MatrixStack, progress: Float, box: Box, borderColor: ARGBColor, contentColor: ARGBColor) {
            enableDepthTest()
            batchRenderTextureColored(matrixStack) {
                pushWidgetTexture(box, BORDER, borderColor)
                pushWidgetTexture(box.copy(width = box.width * progress), CONTENT, contentColor)
            }
            disableDepthTest()
        }

    }
}

