package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.render.pushText
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
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.TntRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.util.LightCoordsUtil
import org.joml.Quaternionf

object TntFuseRenderer : ConfigGroup("tnt") {

    val renderType by configEnum("tnt_fuse", FuseRenderType.None)

    val onlyYRotation by configBoolean("only_y_rotation", false)

    val useMaxLight by configBoolean("use_max_light", true)

    private const val MAX_FUSE = 80f

    private val dangerColor = Color.fromHSV(0f, 1f, 1f)
    private val safeColor = Color.fromHSV(120f / 360f, 1f, 1f)

    @JvmStatic
    fun renderTntFuse(
        tntRenderState: TntRenderState,
        cameraRenderState: CameraRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector,
    ) {
        if (renderType == FuseRenderType.None) return

        val packedLight = useMaxLight.either(LightCoordsUtil.FULL_BRIGHT, tntRenderState.lightCoords)

        val fuse = tntRenderState.fuseRemainingInTicks
        val progress = (fuse / MAX_FUSE).coerceIn(0f, 1f)
        val color = dangerColor.hsvLerp(safeColor, QuadEasing.easeIn(progress))

        val camera = mc.gameRenderer.mainCamera
        val cameraYaw = camera.yRot()
        val cameraPitch = camera.xRot()

        if (renderType == FuseRenderType.ProgressBar) {
            poseStack.pushPose()
            poseStack.translate(0f, 1.35f, 0f)
            // 对齐方向
            poseStack.mulPose(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!onlyYRotation)
                poseStack.mulPose(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
            poseStack.scale(-0.025f, -0.025f, 0.025f)
            val width = 40f
            val height = 8f
            val area = Rect(Offset(x = -width / 2, y = -4f), Size(width, height))
            FuseRenderType.renderArea(poseStack, nodeCollector, progress, area, Colors.WHITE, color, packedLight)
            poseStack.popPose()
        } else if (renderType == FuseRenderType.Text) {
            poseStack.pushPose()
            poseStack.translate(0f, 1.25f, 0f)
            poseStack.mulPose(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!onlyYRotation)
                poseStack.mulPose(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
            poseStack.scale(-0.025f, -0.025f, 0.025f)
            val text = "%.2f".format(fuse)
            val width = text.width
            nodeCollector.pushText(
                text,
                x = -width / 2f,
                y = -4.5f,
                dropShadow = false,
                color = color,
                backgroundColor = Colors.TRANSPARENT,
                outlineColor = Colors.GRAY,
                displayMode = Font.DisplayMode.NORMAL,
                packedLight = packedLight,
                poseStack = poseStack,
            )
            poseStack.popPose()
        }

    }
}

