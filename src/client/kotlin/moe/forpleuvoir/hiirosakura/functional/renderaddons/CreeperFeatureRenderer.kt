package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.functional.renderaddons.TntRenderConfig.RenderType
import moe.forpleuvoir.hiirosakura.util.resetMatricesKeepTranslation
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
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.CreeperEntityModel
import net.minecraft.client.render.entity.state.CreeperEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import org.joml.Quaternionf

class CreeperFeatureRenderer(
    context: FeatureRendererContext<CreeperEntityRenderState, CreeperEntityModel>,
) : FeatureRenderer<CreeperEntityRenderState, CreeperEntityModel>(context) {

    object Config : ModConfigContainer("creeper") {

        val renderType by enum("fuse", RenderType.None)

        val onlyYRotation by keyBindBoolean("only_y_rotation", value = false)

    }

    companion object {

        private const val MAX_FUSE = 1f

    }

    override fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        state: CreeperEntityRenderState,
        limbAngle: Float,
        limbDistance: Float
    ) {
        if (Config.renderType == RenderType.None || state.fuseTime <= 0) return
        if (vertexConsumers is VertexConsumerProvider.Immediate) {
            renderCreeperFuse(
                state,
                mc.textRenderer,
                matrices.resetMatricesKeepTranslation(),
                vertexConsumers,
                light
            )
        }
    }

    private fun renderCreeperFuse(
        state: CreeperEntityRenderState,
        textRenderer: TextRenderer,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider.Immediate,
        light: Int
    ) {
        if (Config.renderType == RenderType.None) return
        val fuse = state.fuseTime.coerceIn(0f..1f)
        val progress = (1f - (fuse / MAX_FUSE)).coerceIn(0f, 1f)
        val color = HSVColor(0f).lerp(HSVColor(120f), progress)

        val camera = mc.gameRenderer.camera
        val cameraYaw = camera.yaw
        val cameraPitch = camera.pitch

        if (Config.renderType == RenderType.Box) {
            matrixStack.push()
            matrixStack.translate(0f, .5f, 0f)
            // 对齐方向
            matrixStack.multiply(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!Config.onlyYRotation.value)
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
        } else if (Config.renderType == RenderType.Text) {
            matrixStack.push()
            matrixStack.multiply(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!Config.onlyYRotation.value)
                matrixStack.multiply(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转
            matrixStack.scale(-0.025f, -0.025f, 0.025f)
            val text = "%.2f".format(MAX_FUSE - fuse)
            val width = textRenderer.getWidth(text)
            textRenderer.renderText(
                vertexConsumers = vertexConsumerProvider,
                positionMatrix = matrixStack.positionMatrix,
                text = text,
                x = -width / 2f,
                y = -20f,
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



