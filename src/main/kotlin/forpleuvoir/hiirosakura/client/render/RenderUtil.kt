package forpleuvoir.hiirosakura.client.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix4f

/**
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.util
 *
 * 文件名 RenderUtil
 *
 * 创建时间 2021/6/22 0:10
 */
object RenderUtil {
	fun drawTexture(
		texture: Identifier?, matrices: MatrixStack, x: Int, y: Int, z: Int, u: Float, v: Float,
		width: Int, height: Int, textureWidth: Int, textureHeight: Int
	) {
		RenderSystem.setShaderTexture(0, texture)
		drawTexture(matrices, x, y, z, width, height, u, v, width, height, textureWidth, textureHeight)
	}

	private fun drawTexture(
		matrices: MatrixStack, x: Int, y: Int, z: Int, width: Int, height: Int, u: Float, v: Float,
		regionWidth: Int, regionHeight: Int, textureWidth: Int, textureHeight: Int
	) {
		drawTexture(
			matrices, x, x + width, y, y + height, z, regionWidth, regionHeight, u, v, textureWidth,
			textureHeight
		)
	}

	private fun drawTexture(
		matrices: MatrixStack, x0: Int, y0: Int, x1: Int, y1: Int, z: Int, regionWidth: Int,
		regionHeight: Int, u: Float, v: Float, textureWidth: Int, textureHeight: Int
	) {
		drawTexturedQuad(
			matrices.peek().positionMatrix, x0, y0, x1, y1, z, (u + 0.0f) / textureWidth.toFloat(),
			(u + regionWidth.toFloat()) / textureWidth.toFloat(), (v + 0.0f) / textureHeight.toFloat(),
			(v + regionHeight.toFloat()) / textureHeight.toFloat()
		)
	}

	private fun drawTexturedQuad(
		matrices: Matrix4f, x0: Int, x1: Int, y0: Int, y1: Int, z: Int, u0: Float, u1: Float,
		v0: Float, v1: Float
	) {
		RenderSystem.setShader { GameRenderer.getPositionTexShader() }
		val bufferBuilder = Tessellator.getInstance().buffer
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
		bufferBuilder.vertex(matrices, x0.toFloat(), y1.toFloat(), z.toFloat()).texture(u0, v1).next()
		bufferBuilder.vertex(matrices, x1.toFloat(), y1.toFloat(), z.toFloat()).texture(u1, v1).next()
		bufferBuilder.vertex(matrices, x1.toFloat(), y0.toFloat(), z.toFloat()).texture(u1, v0).next()
		bufferBuilder.vertex(matrices, x0.toFloat(), y0.toFloat(), z.toFloat()).texture(u0, v0).next()
		bufferBuilder.end()
		BufferRenderer.draw(bufferBuilder)
	}

}