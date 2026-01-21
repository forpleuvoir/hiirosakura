package moe.forpleuvoir.hiirosakura.gui.widget.radialmenu

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexConsumer
import moe.forpleuvoir.hiirosakura.render.HSRenderPipeline
import moe.forpleuvoir.ibukigourd.gui.base.render.IGGuiGraphics
import moe.forpleuvoir.ibukigourd.render.color
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.GuiElementRenderState
import org.joml.Matrix3x2f
import org.joml.Vector2f
import org.joml.Vector2fc
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * 代码参考自[Create](https://github.com/Creators-of-Create/Create)模组
 */
class RadialSectorRenderState(
    val pose: Matrix3x2f,
    val center: Vector2fc,
    startAngle: Float,
    arcAngle: Float,
    innerRadius: Float,
    outerRadius: Float,
    val innerColor: ARGBColor,
    val outerColor: ARGBColor,
) : GuiElementRenderState {

    companion object {

        fun IGGuiGraphics.pushRadialSector(
            center: Vector2fc,
            startAngle: Float,
            arcAngle: Float,
            innerRadius: Float,
            outerRadius: Float,
            innerColor: ARGBColor,
            outerColor: ARGBColor,
            pose: Matrix3x2f = Matrix3x2f(pose()),
        ) {
            this.getGuiRenderState().submitGuiElement(
                RadialSectorRenderState(
                    pose,
                    center,
                    startAngle,
                    arcAngle,
                    innerRadius,
                    outerRadius,
                    innerColor,
                    outerColor
                )
            )
        }

        private val VERTEX_CACHE = object : HashMap<ArcCacheKey, List<Vector2fc>>(5 * 2) {
            override fun put(key: ArcCacheKey, value: List<Vector2fc>): List<Vector2fc>? {
                if (this.size > 100) this.remove(this.keys.random())
                return super.put(key, value)
            }
        }

        private fun getSectorVertices(radius: Float, startAngle: Float, arcAngle: Float): List<Vector2fc> {
            val segmentCount = if (abs(arcAngle) <= 90) 16 else 32
            return VERTEX_CACHE.getOrPut(ArcCacheKey(radius, startAngle, arcAngle)) {
                buildList {
                    val startRad = Math.toRadians((startAngle - 90.0)).toFloat()
                    val sweepRad = Math.toRadians(arcAngle.toDouble()).toFloat()
                    val stepRad = sweepRad / (segmentCount - 1)
                    for (i in 0 until segmentCount) {
                        val currentRad = startRad + i * stepRad
                        add(Vector2f((radius * cos(currentRad.toDouble())).toFloat(), (radius * sin(currentRad.toDouble())).toFloat()))
                    }
                }
            }
        }
    }

    private val bounds: ScreenRectangle

    private val innerVertices: List<Vector2fc> = getSectorVertices(innerRadius, startAngle, arcAngle)

    private val outerVertices: List<Vector2fc> = getSectorVertices(outerRadius, startAngle, arcAngle)

    init {
        var x1 = Float.POSITIVE_INFINITY
        var x2 = Float.NEGATIVE_INFINITY
        var y1 = Float.POSITIVE_INFINITY
        var y2 = Float.NEGATIVE_INFINITY

        innerVertices.forEach { vec ->
            x1 = minOf(x1, vec.x())
            x2 = maxOf(x2, vec.x())
            y1 = minOf(y1, vec.y())
            y2 = maxOf(y2, vec.y())
        }
        outerVertices.forEach { vec ->
            x1 = minOf(x1, vec.x())
            x2 = maxOf(x2, vec.x())
            y1 = minOf(y1, vec.y())
            y2 = maxOf(y2, vec.y())
        }

        bounds = ScreenRectangle(x1.toInt(), y1.toInt(), (x2 - x1).toInt(), (y2 - y1).toInt()).transformMaxBounds(pose)
    }

    override fun pipeline(): RenderPipeline = HSRenderPipeline.POSITION_COLOR_STRIP

    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()

    override fun scissorArea(): ScreenRectangle? = null

    override fun bounds(): ScreenRectangle = bounds

    override fun buildVertices(consumer: VertexConsumer) {
        for (i in innerVertices.indices) {
            var point: Vector2fc = outerVertices[i]
            consumer.addVertexWith2DPose(pose, point.x() + center.x(), point.y() + center.y()).color(outerColor)

            point = innerVertices[i]
            consumer.addVertexWith2DPose(pose, point.x() + center.x(), point.y() + center.y()).color(innerColor)
        }

    }

}