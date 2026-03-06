package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.util.restPoseStackKeepTranslation
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.PlayerRenderState

class ChatBubbleLayer(
    renderer: RenderLayerParent<PlayerRenderState, PlayerModel>
) : RenderLayer<PlayerRenderState, PlayerModel>(renderer) {

    override fun render(poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, renderState: PlayerRenderState, yRot: Float, xRot: Float) {
        if (!ChatBubbleHandler.enabled) return
        mc.level?.players()
            ?.find { player-> player.gameProfile.name == (renderState as AvatarRenderStateAccessor).`hiirosakura$getName`() && !player.isInvisible }
            ?.let { player ->
                ChatBubbleHandler.render(player, packedLight, renderState, poseStack.restPoseStackKeepTranslation(), bufferSource)
            }
    }

}