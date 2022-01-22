package forpleuvoir.hiirosakura.client.feature.chatbubble

import forpleuvoir.hiirosakura.client.feature.chatbubble.ChatBubble.Companion.getInstance
import forpleuvoir.hiirosakura.client.feature.regex.ChatMessageRegex
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import java.util.concurrent.ConcurrentHashMap

/**
 * 聊天消息泡泡
 *
 * 玩家发送聊天消息时 会在头上显示文本内容
 *
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.chatbubble
 *
 * 文件名 HiiroSakuraChatBubble
 *
 * 创建时间 2021/6/12 21:01
 * @author forpleuvoir
 */
object HiiroSakuraChatBubble {
	private val chatShows: MutableMap<String, ChatBubble> = ConcurrentHashMap()

	@JvmStatic
	fun addChatShow(text: Text) {
		val chatMessageRegex = ChatMessageRegex.getInstance(text)
		getInstance(chatMessageRegex)?.let {
			chatShows[chatMessageRegex.playerName!!] = it
		}
	}

	@JvmStatic
	fun render(player: AbstractClientPlayerEntity, dispatcher: EntityRenderDispatcher, matrixStack: MatrixStack) {
		if (chatShows.containsKey(player.entityName)) {
			chatShows[player.entityName]?.apply {
				if (shouldRemove) {
					chatShows.remove(player.entityName)
					return
				}
				render(player, dispatcher, matrixStack)
			}
		}
	}


}
