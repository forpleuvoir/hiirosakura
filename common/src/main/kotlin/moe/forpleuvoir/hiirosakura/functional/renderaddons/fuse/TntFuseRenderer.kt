package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.render.pushText
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
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.TntRenderState
import net.minecraft.client.renderer.state.CameraRenderState
import org.joml.Quaternionf

object TntFuseRenderer : ModConfigContainer("tnt") {

    val renderType by enum("tnt_fuse", FuseRenderType.None)

    val onlyYRotation by boolean("only_y_rotation", false)

    val useMaxLight by boolean("use_max_light", true)

    private const val maxFuse = 80f

    @JvmStatic
    fun renderTntFuse(
        tntRenderState: TntRenderState,
        cameraRenderState: CameraRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector,
    ) {
        if (renderType == FuseRenderType.None) return

        val packedLight = useMaxLight.either(LightTexture.FULL_BRIGHT, tntRenderState.lightCoords)

        val fuse = tntRenderState.fuseRemainingInTicks
        val progress = (fuse / maxFuse).coerceIn(0f, 1f)
        val color = HSVColor(0f).lerp(HSVColor(120f), QuadEasing.easeIn(progress))

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
            val box = Box(x = -width / 2, y = -4f, Size(width, height))
            FuseRenderType.renderBox(poseStack, nodeCollector, progress, box, Colors.WHITE, color, packedLight)
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
                backgroundColor = Color.ofARGB(0),
                outlineColor = Colors.GRAY,
                displayMode = Font.DisplayMode.NORMAL,
                packedLight = packedLight,
                poseStack = poseStack,
            )
            poseStack.popPose()
        }

    }
}

