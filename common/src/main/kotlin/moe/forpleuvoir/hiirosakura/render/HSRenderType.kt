package moe.forpleuvoir.hiirosakura.render

import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.resources.Identifier
import net.minecraft.util.Util
import java.util.function.Function

object HSRenderType {

    val POSITION_TEX_COLOR: Function<Identifier, RenderType> = Util.memoize<Identifier, RenderType> { texture ->
        val state = RenderSetup.builder(HSRenderPipeline.TEXTURE)
            .bufferSize(1536)
            .affectsCrumbling()
            .withTexture("Sampler0",texture)
            .useLightmap()
            .useOverlay()
            .createRenderSetup()
        RenderType.create("position_text_color", state)
    }
}