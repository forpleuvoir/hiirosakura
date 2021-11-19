package forpleuvoir.hiirosakura.client.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.awt.Color

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.util
 *
 * #class_name TextRenderUtil
 *
 * #create_time 2021/6/13 12:43
 */
object TextRenderUtil {
	@JvmStatic
	fun ageColorText(text: String?, age: Float, maxAge: Float, r2g: Boolean = false): MutableText {
		return LiteralText(text).styled { style: Style -> style.withColor(ageColor(age, maxAge, r2g)) }
	}

	private fun ageColor(age: Float, maxAge: Float, r2g: Boolean): Int {
		if (age < 0) return Color(255, 0, 0).rgb
		if (maxAge < 0) return Color(0, 255, 0).rgb
		val maxColor = ((maxAge - age) / maxAge * 255).toInt()
		val minColor = ((1f - (maxAge - age) / maxAge) * 255).toInt()
		val green: Int
		val red: Int
		if (r2g) {
			green = maxColor
			red = minColor
		} else {
			red = maxColor
			green = minColor
		}
		return Color(red, green, 0).rgb
	}

	/**
	 * 在实体上方渲染文本
	 *
	 * @param entity                 目标实体
	 * @param text                   需要渲染的文本
	 * @param dispatcher             [EntityRenderDispatcher]
	 * @param textRenderer           [TextRenderer]
	 * @param matrixStack            [MatrixStack]
	 * @param vertexConsumerProvider [VertexConsumerProvider]
	 * @param light                  亮度
	 */
	@JvmStatic
	fun renderEntityText(
		entity: Entity, text: Text?, dispatcher: EntityRenderDispatcher,
		textRenderer: TextRenderer,
		matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider?,
		light: Int
	) {
		renderEntityText(
			entity, 0.0, text, dispatcher, textRenderer,
			matrixStack, vertexConsumerProvider, light
		)
	}

	/**
	 * 在实体上方渲染文本
	 *
	 * @param entity                 目标实体
	 * @param textHeight             高度
	 * @param text                   需要渲染的文本
	 * @param dispatcher             [EntityRenderDispatcher]
	 * @param textRenderer           [TextRenderer]
	 * @param matrixStack            [MatrixStack]
	 * @param vertexConsumerProvider [VertexConsumerProvider]
	 * @param light                  亮度
	 */
	private fun renderEntityText(
		entity: Entity, textHeight: Double, text: Text?, dispatcher: EntityRenderDispatcher,
		textRenderer: TextRenderer,
		matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider?,
		light: Int
	) {
		val height = entity.height + 0.5f + textHeight
		matrixStack.push()
		matrixStack.translate(0.0, height, 0.0)
		matrixStack.multiply(dispatcher.rotation)
		matrixStack.scale(-0.025f, -0.025f, 0.025f)
		val matrix4f = matrixStack.peek().model
		val g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f)
		val backgroundColor = (g * 255.0f).toInt() shl 24
		val x = (-textRenderer.getWidth(text) / 2).toFloat()
		textRenderer.draw(
			text, x, 0f, -1, false, matrix4f, vertexConsumerProvider, false,
			backgroundColor, light
		)
		matrixStack.pop()
	}

	/**
	 * 在实体上方渲染多行文本
	 *
	 * @param entity                 目标实体
	 * @param text                   需要渲染的文本列表
	 * @param dispatcher             [EntityRenderDispatcher]
	 * @param textRenderer           [TextRenderer]
	 * @param matrixStack            [MatrixStack]
	 * @param vertexConsumerProvider [VertexConsumerProvider]
	 * @param light                  亮度
	 */
	@JvmStatic
	fun renderEntityMultiText(
		entity: Entity, text: List<Text?>, dispatcher: EntityRenderDispatcher,
		textRenderer: TextRenderer,
		matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider?,
		light: Int
	) {
		val textRows = text.size
		var height = (textRows * 0.25f).toDouble()
		for (item in text) {
			height -= 0.25
			renderEntityText(
				entity, height, item, dispatcher, textRenderer,
				matrixStack, vertexConsumerProvider, light
			)
		}
	}
}