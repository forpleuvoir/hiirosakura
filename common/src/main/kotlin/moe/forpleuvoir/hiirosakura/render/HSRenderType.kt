package moe.forpleuvoir.hiirosakura.render

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubble
import moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse.FuseRenderType
import moe.forpleuvoir.hiirosakura.render.HSRenderPipeline.POSITION_TEX_COLOR
import moe.forpleuvoir.hiirosakura.util.resourceLocation
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType

object HSRenderType {

    val FUSE: RenderType = RenderType.create(
        "hs_fuse",
        1536,
        HSRenderPipeline.TEXTURE,
        RenderType.CompositeState.builder()
            .setTextureState(RenderStateShard.TextureStateShard(FuseRenderType.TEXTURE.texture, false))
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .createCompositeState(false)
    )


    val CHAT_BUBBLE: RenderType = RenderType.create(
        "hs_chat_bubble",
        1536,
        RenderPipelines.register(
            RenderPipeline.builder(POSITION_TEX_COLOR)
                .withLocation(resourceLocation("pipeline/chat_bubble"))
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthBias(0f, -3f)
                .build()
        ),
        RenderType.CompositeState.builder()
            .setTextureState(RenderStateShard.TextureStateShard(ChatBubble.TEXTURE.texture, false))
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .createCompositeState(false)
    )

    val CHAT_BUBBLE_ARROW: RenderType = RenderType.create(
        "hs_chat_bubble_arrow",
        1536,
        RenderPipelines.register(
            RenderPipeline.builder(POSITION_TEX_COLOR)
                .withLocation(resourceLocation("pipeline/chat_bubble_arrow"))
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthBias(0f, -10f)
                .build()
        ),
        RenderType.CompositeState.builder()
            .setTextureState(RenderStateShard.TextureStateShard(ChatBubble.TEXTURE.texture, false))
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .createCompositeState(false)
    )

}