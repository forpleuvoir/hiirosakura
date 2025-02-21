package moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.positionMatrix
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.renderText
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.config.item.impl.enum
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.state.TntEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import org.joml.Quaternionf

object TntFuseRenderer : ModConfigContainer("tnt") {

    val renderType by enum("tnt_fuse", FuseRenderType.None)

    val onlyYRotation by keyBindBoolean("only_y_rotation", value = false)

    private const val maxFuse = 80f

    @JvmStatic
    fun renderTntFuse(
        state: TntEntityRenderState,
        textRenderer: TextRenderer,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider.Immediate,
        light: Int
    ) {
        if (renderType == FuseRenderType.None) return
        val fuse = state.fuse
        val progress = (fuse / maxFuse).coerceIn(0f, 1f)
        val color = HSVColor(0f).lerp(HSVColor(120f), progress)

        val camera = mc.gameRenderer.camera
        val cameraYaw = camera.yaw
        val cameraPitch = camera.pitch

        if (renderType == FuseRenderType.Box) {
            matrixStack.push()
            matrixStack.translate(0f, 1.35f, 0f)
            // 对齐方向
            matrixStack.multiply(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!onlyYRotation.value)
                matrixStack.multiply(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
            matrixStack.scale(-0.025f, -0.025f, 0.025f)
            val width = 40f
            val height = 8f
            val box = Box(x = -width / 2, y = 0f, Size(width, height))
            FuseRenderType.renderBox(matrixStack, progress, box, Colors.WHITE, color)
            matrixStack.pop()
        } else if (renderType == FuseRenderType.Text) {
            matrixStack.push()
            matrixStack.multiply(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!onlyYRotation.value)
                matrixStack.multiply(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
            matrixStack.scale(-0.025f, -0.025f, 0.025f)
            val text = "%.2f".format(fuse)
            val width = text.width
            textRenderer.renderText(
                vertexConsumers = vertexConsumerProvider,
                positionMatrix = matrixStack.positionMatrix,
                text = text,
                x = -width / 2f,
                y = -60f,
                shadow = false,
                layerType = TextLayerType.NORMAL,
                color = color,
                backgroundColor = Colors.BLACK.alpha(.05F),
                light = light,
                rightToLeft = false
            )
            matrixStack.pop()
        }

    }
}

