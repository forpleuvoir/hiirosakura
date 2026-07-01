package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import androidx.compose.ui.geometry.Rect
import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon.useIrisCompatiblePipeline
import moe.forpleuvoir.hiirosakura.render.HSRenderType
import moe.forpleuvoir.hiirosakura.render.pushTexture
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.ibukigourd.render.extension.texture.Corner
import moe.forpleuvoir.ibukigourd.render.extension.texture.IGTexture
import moe.forpleuvoir.ibukigourd.render.extension.texture.TextureInfo
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.rendertype.RenderTypes

enum class FuseRenderType {
    Text, ProgressBar, None;

    companion object {

        private val TEXTURE = TextureInfo(16, 16, identifier("texture/gui/fuse.png"))

        private val BORDER = IGTexture(Corner(1), 0, 0, 16, 8, TEXTURE)

        private val CONTENT = IGTexture(Corner(1), 0, 8, 16, 16, TEXTURE)

        private val RENDER_TYPE get() = if (useIrisCompatiblePipeline.enabled) IRIS_RENDER_TYPE else VANILLA_RENDER_TYPE

        private val VANILLA_RENDER_TYPE = HSRenderType.POSITION_TEX_COLOR.apply(TEXTURE.textureId)

        private val IRIS_RENDER_TYPE = RenderTypes.entityTranslucent(TEXTURE.textureId)

        fun renderArea(
            poseStack: PoseStack,
            nodeCollector: SubmitNodeCollector,
            progress: Float,
            area: Rect,
            borderColor: Color,
            contentColor: Color,
            packedLight: Int
        ) {
            nodeCollector.pushTexture(area, BORDER, packedLight, borderColor, poseStack, RENDER_TYPE)
//            nodeCollector.pushTexture(area.copy(width = area.width * progress), CONTENT, packedLight, contentColor, poseStack, RENDER_TYPE)
            nodeCollector.pushTexture(area.copy(right = area.width * progress - area.left), CONTENT, packedLight, contentColor, poseStack, RENDER_TYPE)
        }

    }
}

