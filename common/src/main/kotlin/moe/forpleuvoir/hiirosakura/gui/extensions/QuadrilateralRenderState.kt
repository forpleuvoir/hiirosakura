package moe.forpleuvoir.hiirosakura.gui.extensions

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexConsumer
import moe.forpleuvoir.ibukigourd.render.color
import moe.forpleuvoir.ibukigourd.render.vertex
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.GuiElementRenderState
import org.joml.Matrix3x2f
import org.joml.Vector2fc

class QuadrilateralRenderState(
    private val p1: Vector2fc,
    private val p2: Vector2fc,
    private val p3: Vector2fc,
    private val p4: Vector2fc,
    /**
     * TOP LEFT
     */
    private val col1: ARGBColor,
    /**
     * BOTTOM LEFT
     */
    private val col2: ARGBColor,
    /**
     * BOTTOM RIGHT
     */
    private val col3: ARGBColor,
    /**
     * TOP RIGHT
     */
    private val col4: ARGBColor,
    private val pose: Matrix3x2f,
    private val pipeline: RenderPipeline,
    private val scissorArea: ScreenRectangle?,
    private val bounds: ScreenRectangle? = bounds(p1, p2, p3, p4, pose, scissorArea)
) : GuiElementRenderState {

    companion object {
        private fun bounds(p1: Vector2fc, p2: Vector2fc, p3: Vector2fc, p4: Vector2fc, pose: Matrix3x2f, scissorArea: ScreenRectangle?): ScreenRectangle? {
            val x0 = p1.x().coerceAtLeast(p2.x()).coerceAtLeast(p3.x()).coerceAtLeast(p4.x())
            val y0 = p1.y().coerceAtLeast(p2.y()).coerceAtLeast(p3.y()).coerceAtLeast(p4.y())
            val x1 = p1.x().coerceAtMost(p2.x()).coerceAtMost(p3.x()).coerceAtMost(p4.x())
            val y1 = p1.y().coerceAtMost(p2.y()).coerceAtMost(p3.y()).coerceAtMost(p4.y())
            val screenRectangle = ScreenRectangle(x0.toInt(), y0.toInt(), (x1 - x0).toInt(), (y1 - y0).toInt()).transformMaxBounds(pose)
            return if (scissorArea != null) scissorArea.intersection(screenRectangle) else screenRectangle
        }
    }

    override fun pipeline(): RenderPipeline = pipeline

    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()

    override fun scissorArea(): ScreenRectangle? = scissorArea

    override fun bounds(): ScreenRectangle? = bounds

    override fun buildVertices(consumer: VertexConsumer, z: Float) {
        consumer.vertex(pose, p1.x(), p1.y(), z).color(col1)
        consumer.vertex(pose, p2.x(), p2.y(), z).color(col2)
        consumer.vertex(pose, p3.x(), p3.y(), z).color(col3)
        consumer.vertex(pose, p4.x(), p4.y(), z).color(col4)
    }
}