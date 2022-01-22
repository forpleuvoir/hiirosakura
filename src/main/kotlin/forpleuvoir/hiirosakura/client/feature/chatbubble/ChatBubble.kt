package forpleuvoir.hiirosakura.client.feature.chatbubble

import com.mojang.blaze3d.systems.RenderSystem
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.HiiroSakuraClient.tickCounter
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_BUBBLE_TEXTURE_COLOR
import forpleuvoir.hiirosakura.client.feature.regex.ChatMessageRegex
import forpleuvoir.ibuki_gourd.utils.color.Color4f
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.*

/**
 * 聊天消息泡泡
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.chatbubble
 *
 * 文件名 ChatBubble
 *
 * 创建时间 2021/6/12 21:47
 */
class ChatBubble(private val text: String, private val playerName: String) {
	/**
	 * 显示时间
	 */
	private val timer: Long = tickCounter + Configs.Values.CHAT_BUBBLE_TIME.getValue()
	private val list: List<Text>
	private val textRenderer: TextRenderer = MinecraftClient.getInstance().textRenderer
	var shouldRemove: Boolean = false

	/**
	 * 渲染文本
	 *
	 * @param player      玩家
	 * @param dispatcher  [EntityRenderDispatcher]
	 * @param matrixStack [MatrixStack]
	 */
	fun render(player: AbstractClientPlayerEntity, dispatcher: EntityRenderDispatcher, matrixStack: MatrixStack) {
		if (timer <= tickCounter) {
			shouldRemove = true
			return
		}
		if (player.entityName != playerName) return
		val width = maxWidth
		val count = list.size
		val lineSpacing = 4
		val height = getHeight(count)
		matrixStack.push()
		matrixStack.translate(0.0, player.height + Configs.Values.CHAT_BUBBLE_HEIGHT.getValue(), 0.0)
		val scale = -0.025f * Configs.Values.CHAT_BUBBLE_SCALE.getValue().toFloat()
		matrixStack.scale(scale, scale, scale)
		matrixStack.multiply(dispatcher.rotation)
		renderBackground(matrixStack, width + 5, height + lineSpacing)
		for ((index, text) in list.withIndex()) {
			textRenderer.draw(
				matrixStack,
				text,
				-(width / 2).toFloat(),
				(-height + getHeight(count, index = index)).toFloat(),
				Configs.Values.CHAT_BUBBLE_TEXT_COLOR.getValue().rgba
			)
		}
		matrixStack.pop()
	}

	private fun renderBackground(matrixStack: MatrixStack, width: Int, height: Int) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader)
		RenderSystem.enableDepthTest()
		RenderSystem.setShaderTexture(0, BUBBLE_TEXTURE)
		val color = Color4f().fromInt(CHAT_BUBBLE_TEXTURE_COLOR.getValue().rgba)
		RenderSystem.setShaderColor(color.red, color.green, color.blue, color.alpha)
		matrixStack.translate(0.0, 0.0, -1.0)
		DrawableHelper.drawTexture(matrixStack, -(width / 2) - 4, -height - 4, 4, 4, 0f, 0f, 4, 4, 32, 32)
		DrawableHelper.drawTexture(matrixStack, -(width / 2), -height - 4, width, 4, 4f, 0f, 24, 4, 32, 32)
		DrawableHelper.drawTexture(matrixStack, width / 2, -height - 4, 4, 4, 28f, 0f, 4, 4, 32, 32)
		DrawableHelper.drawTexture(matrixStack, -(width / 2) - 4, -height, 4, height, 0f, 4f, 4, 24, 32, 32)
		DrawableHelper.drawTexture(matrixStack, -(width / 2), -height, width, height, 4f, 4f, 24, 24, 32, 32)
		DrawableHelper.drawTexture(matrixStack, width / 2, -height, 4, height, 28f, 4f, 4, 24, 32, 32)
		DrawableHelper.drawTexture(matrixStack, -(width / 2) - 4, 0, 4, 4, 0f, 28f, 4, 4, 32, 32)
		DrawableHelper.drawTexture(matrixStack, -(width / 2), 0, width, 4, 4f, 28f, 24, 4, 32, 32)
		DrawableHelper.drawTexture(matrixStack, width / 2, 0, 4, 4, 28f, 28f, 4, 4, 32, 32)
		matrixStack.translate(0.0, 0.0, 1.0)
		RenderSystem.disableDepthTest()
	}

	/**
	 * 处理文本
	 * 多字符换行并去掉玩家名
	 *
	 * @return 处理之后的文本 [List]
	 */
	private fun textHandler(maxWidth: Int): List<Text> {
		val list: MutableList<Text> = LinkedList()
		val sb = StringBuilder()
		for (c in text.toCharArray()) {
			if (textRenderer.getWidth(sb.toString()) > maxWidth) {
				list.add(LiteralText(sb.toString()))
				sb.clear()
			}
			sb.append(c)
		}
		list.add(LiteralText(sb.toString()))
		return list
	}

	private val maxWidth: Int
		get() {
			var max = 0
			for (text in list) {
				if (max < textRenderer.getWidth(text)) {
					max = textRenderer.getWidth(text)
				}
			}
			return max
		}

	private fun getHeight(count: Int, textHeight: Int = 9, lineSpacing: Int = 4): Int {
		var height = textHeight
		for (i in 0 until count) {
			if (i != 0) {
				height += textHeight + lineSpacing
			}
		}
		return height
	}

	private fun getHeight(count: Int, textHeight: Int = 9, lineSpacing: Int = 4, index: Int): Int {
		var height = -lineSpacing / 2
		for (i in 0 until count) {
			if (i != 0) {
				height += textHeight + lineSpacing
			}
			if (i == index) {
				return height
			}
		}
		return height
	}

	companion object {
		val BUBBLE_TEXTURE = Identifier(
			HiiroSakuraClient.modId,
			"texture/gui/feature/chatshow/bubble.png"
		)

		@JvmStatic
		fun getInstance(chatMessageRegex: ChatMessageRegex): ChatBubble? {
			return try {
				chatMessageRegex.message?.let { message -> chatMessageRegex.playerName?.let { playerName -> ChatBubble(message, playerName) } }
			} catch (ignored: Exception) {
				null
			}
		}
	}

	init {
		list = textHandler(Configs.Values.CHAT_BUBBLE_TEXT_MAX_WIDTH.getValue())
	}
}