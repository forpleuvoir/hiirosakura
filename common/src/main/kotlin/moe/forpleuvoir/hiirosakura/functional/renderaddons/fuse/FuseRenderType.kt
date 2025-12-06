package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.render.HSRenderType
import moe.forpleuvoir.hiirosakura.render.pushTexture
import moe.forpleuvoir.hiirosakura.util.resourceLocation
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.client.renderer.SubmitNodeCollector

enum class FuseRenderType {
    Text, Box, None;

    companion object {

        val TEXTURE = TextureInfo(16, 16, resourceLocation("texture/gui/fuse.png"))

        val BORDER = WidgetTexture(Corner(1), 0, 0, 16, 8, TEXTURE)

        val CONTENT = WidgetTexture(Corner(1), 0, 8, 16, 16, TEXTURE)

        fun renderBox(
            poseStack: PoseStack,
            nodeCollector: SubmitNodeCollector,
            progress: Float,
            box: Box,
            borderColor: ARGBColor,
            contentColor: ARGBColor,
            packedLight: Int
        ) {
            nodeCollector.pushTexture(box, BORDER, packedLight, borderColor, poseStack, HSRenderType.FUSE)
            nodeCollector.pushTexture(box.copy(width = box.width * progress), CONTENT, packedLight, contentColor, poseStack, HSRenderType.FUSE)
        }

    }
}

