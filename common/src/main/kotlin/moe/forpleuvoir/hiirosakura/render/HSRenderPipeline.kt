package moe.forpleuvoir.hiirosakura.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.renderpearl.api.pipeline.BlendFunction
import com.mojang.renderpearl.api.pipeline.ColorTargetState
import com.mojang.renderpearl.api.pipeline.DepthStencilState
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import moe.forpleuvoir.hiirosakura.util.identifier
import net.minecraft.client.renderer.BindGroupLayouts
import net.minecraft.client.renderer.RenderPipelines

/**
 * 本项目自有的渲染管线。
 *
 * 管线描述位于 `com.mojang.renderpearl.api.pipeline`，按「绑定组布局 + 顶点绑定 + 图元拓扑」三段式构建：
 * 矩阵来自 [BindGroupLayouts.PROJECTION]（投影矩阵）与 [BindGroupLayouts.DYNAMIC_TRANSFORMS]（动态变换矩阵），
 * 顶点格式经 `withVertexBinding` 绑定，图元拓扑取 [PrimitiveTopology]；
 * 可复用的公共片段常量不对外公开，本对象的片段一律自行构建。
 */
object HSRenderPipeline {

    /**
     * 纹理片段（位置 + UV + 颜色，四边形）的公共描述。
     *
     * `core/position_tex_color` 声明 Position / UV0 / Color 三个顶点输入、Projection 与 DynamicTransforms 两个 UBO 以及 Sampler0，
     * 因此顶点绑定取 [DefaultVertexFormat.POSITION_TEX_COLOR]，绑定组为投影矩阵、动态变换矩阵与 Sampler0，
     * 不采样光照贴图与 overlay。
     */
    val POSITION_TEX_COLOR: RenderPipeline.Snippet = RenderPipeline.builder()
        .withVertexShader("core/position_tex_color")
        .withFragmentShader("core/position_tex_color")
        .withBindGroupLayout(BindGroupLayouts.PROJECTION)
        .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
        .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
        .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .buildSnippet()

    /**
     * 半透明纹理管线：世界中的四边形按默认深度状态绘制（被地形遮挡、写深度），
     * 并关闭背面剔除——镜像姿势下的四边形可见面朝向与原版约定相反。
     */
    val TEXTURE: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(POSITION_TEX_COLOR)
            .withLocation(identifier("pipeline/texture"))
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .build()
    )

    /** 纯色三角带。 */
    val POSITION_COLOR_STRIP: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder()
            .withLocation(identifier("pipeline/position_color_strip"))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withBindGroupLayout(BindGroupLayouts.PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
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
            .withBindGroupLayout(BindGroupLayouts.PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .build()
    )
}
