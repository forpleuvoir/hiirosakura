package moe.forpleuvoir.hiirosakura.render

import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

object HSRenderType {

    val POSITION_TEX_COLOR: Function<ResourceLocation, RenderType> = Util.memoize<ResourceLocation, RenderType> { texture ->
        val state = RenderType.CompositeState.builder()
            .setTextureState(RenderStateShard.TextureStateShard(texture, false))
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .createCompositeState(true)
        RenderType.create("position_text_color", 1536, true, false, HSRenderPipeline.TEXTURE, state)
    }
}