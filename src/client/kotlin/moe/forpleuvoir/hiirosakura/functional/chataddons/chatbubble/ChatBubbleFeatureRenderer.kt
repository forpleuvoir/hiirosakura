package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import moe.forpleuvoir.hiirosakura.util.resetMatricesKeepTranslation
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.entity.state.PlayerEntityRenderState
import net.minecraft.client.util.math.MatrixStack

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
            ChatBubbleHandler.render(state.name, matrices.resetMatricesKeepTranslation(), vertexConsumers, light)
        }
    }

}