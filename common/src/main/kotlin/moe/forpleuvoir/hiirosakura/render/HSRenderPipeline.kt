package moe.forpleuvoir.hiirosakura.render

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import moe.forpleuvoir.hiirosakura.util.identifier
import net.minecraft.client.renderer.RenderPipelines

object HSRenderPipeline {

    val POSITION_TEX_COLOR: RenderPipeline.Snippet = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
        .withVertexShader("core/position_tex_color")
        .withFragmentShader("core/position_tex_color")
        .withSampler("Sampler0")
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR, VertexFormat.Mode.QUADS)
        .buildSnippet()

    val TEXTURE: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(POSITION_TEX_COLOR)
            .withLocation(identifier("pipeline/texture"))
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
    )


}