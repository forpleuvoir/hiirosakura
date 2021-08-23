package forpleuvoir.hiirosakura.client.feature.chatshow

import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.chatshow.ChatShow.Companion.getInstance
import forpleuvoir.hiirosakura.client.util.HSLogger
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer

/**
 * 聊天消息显示
 *
 * 玩家发送聊天消息时 会在头上显示文本内容
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.chatshow
 *
 * #class_name HiiroSakuraChatShow
 *
 * #create_time 2021/6/12 21:01
 */
object HiiroSakuraChatShow {
	private val log = HSLogger.getLogger(HiiroSakuraChatShow::class.java)
	private val chatShows: MutableMap<String, ChatShow> = ConcurrentHashMap()
	private val removeList: Queue<String> = ConcurrentLinkedQueue()

	@JvmStatic
	fun addChatShow(text: Text?) {
		val chatMessageRegex = HiiroSakuraData.SERVER_CHAT_MESSAGE_REGEX.chatMessageRegex(text)
		val chatShow = getInstance(chatMessageRegex)
		if (chatMessageRegex != null && chatShow != null) {
			chatMessageRegex.playerName?.let { chatShows.put(it, chatShow) }
		}

	}

	@JvmStatic
	fun render(
		player: AbstractClientPlayerEntity, dispatcher: EntityRenderDispatcher,
		matrixStack: MatrixStack
	) {
		removeList.forEach(Consumer { key: String -> chatShows.remove(key) })
		removeList.clear()
		chatShows.forEach { (_: String?, chatShow: ChatShow) ->
			chatShow.render(player, dispatcher, matrixStack)
		}
	}

	fun remove(playerName: String) {
		removeList.add(playerName)
	}
}
