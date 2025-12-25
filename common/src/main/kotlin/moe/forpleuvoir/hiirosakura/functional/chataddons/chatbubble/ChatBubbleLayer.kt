package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.util.restPoseStackKeepTranslation
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.model.player.PlayerModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.AvatarRenderState

class ChatBubbleLayer(
    renderer: RenderLayerParent<AvatarRenderState, PlayerModel>
) : RenderLayer<AvatarRenderState, PlayerModel>(renderer) {

    override fun submit(poseStack: PoseStack, nodeCollector: SubmitNodeCollector, packedLight: Int, renderState: AvatarRenderState, yRot: Float, xRot: Float) {
        if (!ChatBubbleHandler.enabled) return
        mc.level?.players()
            ?.find { player -> player.gameProfile.name == (renderState as AvatarRenderStateAccessor).`hiirosakura$getName`() && !player.isInvisible }
            ?.let { player ->
                ChatBubbleHandler.render(player, packedLight, renderState, poseStack.restPoseStackKeepTranslation(), nodeCollector)
            }
    }

}