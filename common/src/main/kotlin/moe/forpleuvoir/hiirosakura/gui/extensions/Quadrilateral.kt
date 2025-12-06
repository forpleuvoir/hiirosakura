package moe.forpleuvoir.hiirosakura.gui.extensions

import com.mojang.blaze3d.pipeline.RenderPipeline
import moe.forpleuvoir.ibukigourd.gui.base.render.IGGuiGraphics
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.client.renderer.RenderPipelines
import org.joml.Matrix3x2f
import org.joml.Vector2f
import org.joml.Vector2fc

/**
 * 顶点为逆时针顺序
 */
data class Quadrilateral(
    val p1: Vector2fc,
    val p2: Vector2fc,
    val p3: Vector2fc,
    val p4: Vector2fc
) {
    // 判断点是否在四边形内部或边界上
    operator fun contains(vec2f: Vector2fc): Boolean {
        // 定义辅助函数：判断两条线段是否相交
        fun isIntersecting(
            p1: Vector2fc, p2: Vector2fc,
            q1: Vector2fc, q2: Vector2fc
        ): Boolean {
            // 叉积计算函数
            fun cross(v1x: Float, v1y: Float, v2x: Float, v2y: Float): Float {
                return v1x * v2y - v1y * v2x
            }

            // 计算向量 p1->p2 和 p1->q1，p1->q2 的叉积
            val d1 = cross(q1.x() - p1.x(), q1.y() - p1.y(), p2.x() - p1.x(), p2.y() - p1.y())
            val d2 = cross(q2.x() - p1.x(), q2.y() - p1.y(), p2.x() - p1.x(), p2.y() - p1.y())
            val d3 = cross(p1.x() - q1.x(), p1.y() - q1.y(), q2.x() - q1.x(), q2.y() - q1.y())
            val d4 = cross(p2.x() - q1.x(), p2.y() - q1.y(), q2.x() - q1.x(), q2.y() - q1.y())

            // 判断是否相交：叉积符号不同
            return d1 * d2 < 0 && d3 * d4 < 0
        }

        // 定义射线终点，随意取一个远点（确保远离四边形）
        val rayEndPoint = Vector2f(1e9f, vec2f.y())

        // 定义四边形四条边
        val edges = listOf(
            Pair(p1, p2),
            Pair(p2, p3),
            Pair(p3, p4),
            Pair(p4, p1)
        )

        // 计算射线与四边形四条边的交点数
        var intersections = 0
        for ((start, end) in edges) {
            if (isIntersecting(vec2f, rayEndPoint, start, end)) {
                intersections++
            }
        }

        // 射线交点数为奇数，则点在内部；否则在外部
        return intersections % 2 == 1
    }
}

fun IGGuiGraphics.pushQuad(
    quad: Quadrilateral,
    color: ARGBColor,
    pose: Matrix3x2f = Matrix3x2f(pose()),
    pipeline: RenderPipeline = RenderPipelines.GUI,
    scissorBox: Box? = peekScissorBox()
) = getGuiRenderState().submitGuiElement(
    QuadrilateralRenderState(
        quad.p1, quad.p2, quad.p3, quad.p4,
        _applyModulatedColor(color), _applyModulatedColor(color), _applyModulatedColor(color), _applyModulatedColor(color),
        pose, pipeline,
        scissorBox?.asScreenRectangle
    )
)

private val transparent = Color(0, 0, 0, 0)

private fun IGGuiGraphics._applyModulatedColor(color: ARGBColor): ARGBColor {
    return if (colorModulator.argb == -1) color
    else if (colorModulator.alpha == 0) transparent
    else color * colorModulator
}
