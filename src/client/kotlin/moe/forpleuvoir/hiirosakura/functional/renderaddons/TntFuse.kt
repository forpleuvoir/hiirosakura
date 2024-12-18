package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderBox
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.positionMatrix
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.renderText
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
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

object TntRenderConfig : ModConfigContainer("tnt") {

    enum class RenderType {
        Text, Box, None
    }

    val renderType by enum("tnt_fuse", RenderType.None)

    val onlyYRotation by keyBindBoolean("only_y_rotation", value = false)

}

private const val maxFuse = 80f

internal fun renderTntFuse(
    state: TntEntityRenderState,
    textRenderer: TextRenderer,
    matrixStack: MatrixStack,
    vertexConsumerProvider: VertexConsumerProvider.Immediate,
    light: Int
) {
    if (TntRenderConfig.renderType == TntRenderConfig.RenderType.None) return
    val fuse = state.fuse
    val progress = (fuse / maxFuse).coerceIn(0f, 1f)
    val color = HSVColor(0f).lerp(HSVColor(120f), progress)

    val camera = mc.gameRenderer.camera
    val cameraYaw = camera.yaw
    val cameraPitch = camera.pitch

    if (TntRenderConfig.renderType == TntRenderConfig.RenderType.Box) {
        matrixStack.push()
        matrixStack.translate(0f, 1.25f, 0f)
        // 对齐方向
        matrixStack.multiply(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
        if (!TntRenderConfig.onlyYRotation.value)
            matrixStack.multiply(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
        matrixStack.scale(-1f, -1f, 1f)
        val width = 0.8f
        val height = 0.1f
        val box = Box(x = -width / 2, y = 0f, Size(width, height))
        batchRenderBox(
            vertexConsumerProvider,
            matrixStack,
        ) {
            pushBoxOutline(box, Colors.WHITE, borderSize = 0.01f, inner = false)
            pushBox(box.copy(width = width * progress), color)
        }
        matrixStack.pop()
    } else if (TntRenderConfig.renderType == TntRenderConfig.RenderType.Text) {
        matrixStack.push()
        matrixStack.multiply(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
        if (!TntRenderConfig.onlyYRotation.value)
            matrixStack.multiply(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
        matrixStack.scale(-0.025f, -0.025f, 0.025f)
        val text = "%.2f".format(fuse)
        val width = textRenderer.getWidth(text)
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