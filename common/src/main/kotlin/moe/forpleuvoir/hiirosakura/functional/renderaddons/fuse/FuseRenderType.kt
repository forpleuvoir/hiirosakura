package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon.useIrisCompatiblePipeline
import moe.forpleuvoir.hiirosakura.render.HSRenderType
import moe.forpleuvoir.hiirosakura.render.pushTexture
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.rendertype.RenderTypes

enum class FuseRenderType {
    Text, ProgressBar, None;

    companion object {

        private val TEXTURE = TextureInfo(16, 16, identifier("texture/gui/fuse.png"))

        private val BORDER = WidgetTexture(Corner(1), 0, 0, 16, 8, TEXTURE)

        private val CONTENT = WidgetTexture(Corner(1), 0, 8, 16, 16, TEXTURE)

        private val RENDER_TYPE get() = if (useIrisCompatiblePipeline.value) IRIS_RENDER_TYPE else VANILLA_RENDER_TYPE

        private val VANILLA_RENDER_TYPE = HSRenderType.POSITION_TEX_COLOR.apply(TEXTURE.texture)

        private val IRIS_RENDER_TYPE = RenderTypes.entityTranslucent(TEXTURE.texture)

        fun renderBox(
            poseStack: PoseStack,
            nodeCollector: SubmitNodeCollector,
            progress: Float,
            box: Box,
            borderColor: ARGBColor,
            contentColor: ARGBColor,
            packedLight: Int
        ) {
            nodeCollector.pushTexture(box, BORDER, packedLight, borderColor, poseStack, RENDER_TYPE)
            nodeCollector.pushTexture(box.copy(width = box.width * progress), CONTENT, packedLight, contentColor, poseStack, RENDER_TYPE)
        }

    }
}

