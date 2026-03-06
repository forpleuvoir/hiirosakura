package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.render.pushText
import moe.forpleuvoir.hiirosakura.util.restPoseStackKeepTranslation
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.math.bezier.QuadEasing
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.util.primitive.either
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.enum
import net.minecraft.client.gui.Font
import net.minecraft.client.model.CreeperModel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.CreeperRenderState
import org.joml.Quaternionf

class CreeperFuseLayer(
    renderer: RenderLayerParent<CreeperRenderState, CreeperModel>
) : RenderLayer<CreeperRenderState, CreeperModel>(renderer) {

    companion object : ModConfigContainer("creeper") {

        private const val MAX_FUSE = 1f

        val renderType by enum("fuse", FuseRenderType.None)

        val onlyYRotation by boolean("only_y_rotation", false)

        val useMaxLight by boolean("use_max_light", true)

    }

    override fun render(poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, renderState: CreeperRenderState, yRot: Float, xRot: Float) {
        if (renderType == FuseRenderType.None || renderState.swelling <= 0) return
        renderCreeperFuse(renderState, poseStack.restPoseStackKeepTranslation(), bufferSource, packedLight)
    }

    private fun renderCreeperFuse(
        state: CreeperRenderState,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        if (renderType == FuseRenderType.None) return

        val packedLight = useMaxLight.either(LightTexture.FULL_BRIGHT, packedLight)
        val fuse = state.swelling.coerceIn(0f..1f)
        val progress = (1f - (fuse / MAX_FUSE)).coerceIn(0f, 1f)
        val color = HSVColor(0f).lerp(HSVColor(120f), QuadEasing.easeIn(progress))

        val camera = mc.gameRenderer.mainCamera
        val cameraYaw = camera.yRot
        val cameraPitch = camera.xRot
        val h = state.boundingBoxHeight * 0.35f
        if (renderType == FuseRenderType.ProgressBar) {
            poseStack.pushPose()
            poseStack.translate(0f, h, 0f)
            // 对齐方向
            poseStack.mulPose(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!onlyYRotation)
                poseStack.mulPose(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
            poseStack.scale(-0.025f, -0.025f, 0.025f)
            val width = 40f
            val height = 8f
            val box = Box(x = -width / 2, y = -4f, Size(width, height))
            FuseRenderType.renderBox(poseStack, bufferSource, progress, box, Colors.WHITE, color, packedLight)
            poseStack.popPose()
        } else if (renderType == FuseRenderType.Text) {
            poseStack.pushPose()
            poseStack.translate(0f, h, 0f)
            poseStack.mulPose(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!onlyYRotation)
                poseStack.mulPose(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
            poseStack.scale(-0.025f, -0.025f, 0.025f)
            val text = "%.2f".format((MAX_FUSE - fuse) * 100)
            val width = text.width
            mc.font.pushText(
                text,
                -width / 2f,
                -4.5f,
                false,
                Font.DisplayMode.NORMAL,
                packedLight,
                color,
                Color.ofARGB(0),
                poseStack,
                bufferSource
            )
            poseStack.popPose()
        }

    }

}



