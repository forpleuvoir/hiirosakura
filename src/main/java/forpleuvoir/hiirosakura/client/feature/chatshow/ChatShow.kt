package forpleuvoir.hiirosakura.client.feature.chatshow

import com.mojang.blaze3d.systems.RenderSystem
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.HiiroSakuraClient.tickCounter
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.feature.regex.ChatMessageRegex
import forpleuvoir.hiirosakura.client.render.RenderUtil
import forpleuvoir.hiirosakura.client.util.HSLogger
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.*

/**
 * 聊天消息显示
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.chatshow
 *
 * #class_name ChatShow
 *
 * #create_time 2021/6/12 21:47
 */
class ChatShow(
	/**
	 * 文本
	 */
	private val text: String, private val playerName: String
) {
	/**
	 * 显示时间
	 */
	private val timer: Long = tickCounter + Configs.Values.CHAT_SHOW_TIME.integerValue
	private val list: List<Text>
	private val textRenderer: TextRenderer = MinecraftClient.getInstance().textRenderer

	/**
	 * 渲染文本
	 *
	 * @param player      玩家
	 * @param dispatcher  [EntityRenderDispatcher]
	 * @param matrixStack [MatrixStack]
	 */
	fun render(
		player: AbstractClientPlayerEntity, dispatcher: EntityRenderDispatcher, matrixStack: MatrixStack
	) {
		if (timer <= tickCounter) {
			HiiroSakuraChatShow.remove(playerName)
			return
		}
		if (player.entityName != playerName) return
		val width = maxWidth
		val count = list.size
		val lineSpacing = 4
		val textHeight = 9
		val height = getHeight(count, textHeight, lineSpacing)
		matrixStack.push()
		matrixStack.translate(0.0, player.height + Configs.Values.CHAT_SHOW_HEIGHT.doubleValue, 0.0)
		val scale = -0.025f * Configs.Values.CHAT_SHOW_SCALE.doubleValue.toFloat()
		matrixStack.scale(scale, scale, scale)
		matrixStack.multiply(dispatcher.rotation)
		renderBackground(matrixStack, width + 5, height + lineSpacing)
		for ((index, text) in list.withIndex()) {
			textRenderer
				.draw(
					matrixStack, text, -(width / 2).toFloat(), (
							-height + getHeight(count, textHeight, lineSpacing, index)).toFloat(),
					Configs.Values.CHAT_SHOW_TEXT_COLOR.integerValue
				)
		}
		matrixStack.pop()
	}

	private fun renderBackground(matrixStack: MatrixStack, width: Int, height: Int) {
		RenderSystem.enableDepthTest()
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE1, matrixStack, -(width / 2) - 12, -height - 12, -1, 0.0f, 0.0f,
			12, 12, 12, 12
		)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE2, matrixStack, -(width / 2), -height - 12, -1, 0.0f, 0.0f,
			width, 12, width, 12
		)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE3, matrixStack, width / 2, -height - 12, -1, 0.0f, 0.0f,
			12, 12, 12, 12
		)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE4, matrixStack, -(width / 2) - 12, -height, -1, 0.0f, 0.0f,
			12, height, 12, height
		)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE5, matrixStack, -(width / 2), -height, -1, 0.0f, 0.0f,
			width, height, width, height
		)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE6, matrixStack, width / 2, -height, -1, 0.0f, 0.0f,
			12, height, 12, height
		)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE7, matrixStack, -(width / 2) - 12, 0, -1, 0.0f, 0.0f,
			12, 12, 12, 12
		)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE8, matrixStack, -(width / 2), 0, -1, 0.0f, 0.0f,
			width, 12, width, 12
		)
		RenderUtil.drawTexture(
			BACKGROUND_TEXTURE9, matrixStack, width / 2, 0, -1, 0.0f, 0.0f,
			12, 12, 12, 12
		)
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
		var sb = StringBuilder()
		for (c in text.toCharArray()) {
			if (textRenderer.getWidth(sb.toString()) > maxWidth) {
				list.add(LiteralText(sb.toString()))
				sb = StringBuilder()
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

	private fun getHeight(count: Int, textHeight: Int, lineSpacing: Int): Int {
		var height = textHeight
		for (i in 0 until count) {
			if (i != 0) {
				height += textHeight + lineSpacing
			}
		}
		return height
	}

	private fun getHeight(count: Int, textHeight: Int, lineSpacing: Int, index: Int): Int {
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
		@Transient
		private val log = HSLogger.getLogger(ChatShow::class.java)
		val BACKGROUND_TEXTURE1 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/1.png"
		)
		val BACKGROUND_TEXTURE2 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/2.png"
		)
		val BACKGROUND_TEXTURE3 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/3.png"
		)
		val BACKGROUND_TEXTURE4 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/4.png"
		)
		val BACKGROUND_TEXTURE5 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/5.png"
		)
		val BACKGROUND_TEXTURE6 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/6.png"
		)
		val BACKGROUND_TEXTURE7 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/7.png"
		)
		val BACKGROUND_TEXTURE8 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/8.png"
		)
		val BACKGROUND_TEXTURE9 = Identifier(
			HiiroSakuraClient.MOD_ID,
			"texture/gui/feature/chatshow/bubbles/9.png"
		)

		@JvmStatic
		fun getInstance(chatMessageRegex: ChatMessageRegex?): ChatShow? {
			return if (chatMessageRegex == null) null else try {
				ChatShow(chatMessageRegex.message, chatMessageRegex.playerName)
			} catch (ignored: Exception) {
				null
			}
		}
	}

	init {
		list = textHandler(Configs.Values.CHAT_SHOW_TEXT_MAX_WIDTH.integerValue)
	}
}