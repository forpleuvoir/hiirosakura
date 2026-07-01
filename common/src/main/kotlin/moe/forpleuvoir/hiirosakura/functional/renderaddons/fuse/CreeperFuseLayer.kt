package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.render.pushText
import moe.forpleuvoir.hiirosakura.util.restPoseStackKeepTranslation
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.math.easing.QuadEasing
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configBoolean
import moe.forpleuvoir.nebula.config.item.configEnum
import net.minecraft.client.gui.Font
import net.minecraft.client.model.monster.creeper.CreeperModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.CreeperRenderState
import net.minecraft.util.LightCoordsUtil
import org.joml.Quaternionf

class CreeperFuseLayer(
    renderer: RenderLayerParent<CreeperRenderState, CreeperModel>
) : RenderLayer<CreeperRenderState, CreeperModel>(renderer) {

    companion object : ConfigGroup("creeper") {

        private const val MAX_FUSE = 1f

        val renderType by configEnum("fuse", FuseRenderType.None)

        val onlyYRotation by configBoolean("only_y_rotation", false)

        val useMaxLight by configBoolean("use_max_light", true)

    }


    override fun submit(
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector,
        packedLight: Int,
        renderState: CreeperRenderState,
        yRot: Float,
        xRot: Float
    ) {
        if (renderType == FuseRenderType.None || renderState.swelling <= 0) return
        renderCreeperFuse(renderState, poseStack.restPoseStackKeepTranslation(), nodeCollector, packedLight)
    }

    private val dangerColor = Color.fromHSV(0f, 1f, 1f)
    private val safeColor = Color.fromHSV(120f / 360f, 1f, 1f)

    private fun renderCreeperFuse(
        state: CreeperRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector,
        packedLight: Int
    ) {
        if (renderType == FuseRenderType.None) return

        val packedLight = useMaxLight.either(LightCoordsUtil.FULL_BRIGHT, packedLight)
        val fuse = state.swelling.coerceIn(0f..1f)
        val progress = (1f - (fuse / MAX_FUSE)).coerceIn(0f, 1f)
        val color = dangerColor.hsvLerp(safeColor, QuadEasing.easeIn(progress))

        val camera = mc.gameRenderer.mainCamera
        val cameraYaw = camera.yRot()
        val cameraPitch = camera.xRot()
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
            val box = Rect(Offset(x = -width / 2, y = -4f), Size(width, height))
            FuseRenderType.renderArea(poseStack, nodeCollector, progress, box, Colors.WHITE, color, packedLight)
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
            nodeCollector.pushText(
                text,
                -width / 2f,
                -4.5f,
                false,
                Font.DisplayMode.NORMAL,
                packedLight,
                color,
                Color.fromARGB(0),
                Colors.GRAY,
                poseStack
            )
            poseStack.popPose()
        }

    }

}



