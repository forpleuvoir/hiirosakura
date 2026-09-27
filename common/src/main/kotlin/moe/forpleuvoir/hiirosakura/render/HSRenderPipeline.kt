package moe.forpleuvoir.hiirosakura.render

import com.mojang.blaze3d.PrimitiveTopology
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.ColorTargetState
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import moe.forpleuvoir.hiirosakura.util.identifier
import net.minecraft.client.renderer.BindGroupLayouts
import net.minecraft.client.renderer.RenderPipelines

/**
 * 本项目自有的渲染管线。
 *
 * Minecraft 26.2 把 Blaze3D 的管线描述改成「绑定组布局 + 顶点绑定 + 图元拓扑」三段式：
 * 旧版的 `withSampler` / `withVertexFormat(format, mode)` / `MATRICES_PROJECTION_SNIPPET`
 * 现在分别由 [BindGroupLayouts]、`withVertexBinding` 和 [PrimitiveTopology] 表达；
 * 且可复用的公共片段常量已不再公开，本对象的片段一律自行构建。
 */
object HSRenderPipeline {

    /** 纹理片段（位置 + UV + 光照 + 颜色，四边形）的公共描述。 */
    val POSITION_TEX_COLOR: RenderPipeline.Snippet = RenderPipeline.builder()
        .withVertexShader("core/position_tex_color")
        .withFragmentShader("core/position_tex_color")
        .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
        .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
        .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR)
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .buildSnippet()

    /** 半透明纹理管线。 */
    val TEXTURE: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(POSITION_TEX_COLOR)
            .withLocation(identifier("pipeline/texture"))
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .build()
    )

    /** 纯色三角带。 */
    val POSITION_COLOR_STRIP: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder()
            .withLocation(identifier("pipeline/position_color_strip"))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP)
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .build()
    )

    /** 纯色四边形。 */
    val POSITION_COLOR_QUADS: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder()
            .withLocation(identifier("pipeline/position_color_quads"))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .build()
    )
}
