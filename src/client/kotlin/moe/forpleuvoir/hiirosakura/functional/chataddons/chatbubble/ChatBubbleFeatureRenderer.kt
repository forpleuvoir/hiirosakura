package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.entity.state.PlayerEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3f

class ChatBubbleFeatureRenderer(
    context: FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel>,
) : FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel>(context) {

    override fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        state: PlayerEntityRenderState,
        limbAngle: Float,
        limbDistance: Float
    ) {
        if (!ChatBubbleHandler.Config.enabled.value) return
        if (vertexConsumers is VertexConsumerProvider.Immediate) {
            //需要清除原矩阵栈的旋转数据
            ChatBubbleHandler.render(state.name, resetMatricesKeepTranslation(matrices), vertexConsumers, light)
        }
    }

    private fun resetMatricesKeepTranslation(oldMatrices: MatrixStack): MatrixStack {
        val translation = oldMatrices.peek().positionMatrix.getTranslation(Vector3f())
        val newMatrices = MatrixStack()
        newMatrices.loadIdentity()
        newMatrices.translate(translation.x(), translation.y(), translation.z())
        return newMatrices
    }
}