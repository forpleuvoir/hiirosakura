package forpleuvoir.hiirosakura.client.feature.chatbubble

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.feature.chatbubble.ChatBubble.Companion.getInstance
import forpleuvoir.hiirosakura.client.feature.regex.ChatMessageRegex
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.common.IModInitialize
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.Shader
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType.CLIENT_RESOURCES
import net.minecraft.text.Text
import net.minecraft.util.Identifier
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
object HiiroSakuraChatBubble : IModInitialize {
	private val log = HSLogger.getLogger(this.javaClass)
	private val chatBubbles: MutableMap<String, ChatBubble> = ConcurrentHashMap()


	@JvmStatic
	fun addChatBubble(text: Text) {
		val chatMessageRegex = ChatMessageRegex.getInstance(text)
		getInstance(chatMessageRegex)?.let {
			chatBubbles[chatMessageRegex.playerName!!] = it
		}
	}

	@JvmStatic
	fun render(player: AbstractClientPlayerEntity, dispatcher: EntityRenderDispatcher, matrixStack: MatrixStack) {
		if (chatBubbles.containsKey(player.entityName)) {
			chatBubbles[player.entityName]?.apply {
				if (shouldRemove) {
					chatBubbles.remove(player.entityName)
					return
				}
				render(player, dispatcher, matrixStack)
			}
		}
	}

	var shader: Shader? = null

	override fun initialize() {
		ResourceManagerHelper.get(CLIENT_RESOURCES).registerReloadListener(object : SimpleSynchronousResourceReloadListener {

			override fun reload(manager: ResourceManager) {
				try {
					shader = Shader(manager, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR)
				} catch (e: Exception) {
					log.error("Error occurred while loading resource shader position_tex_color ", e)
				}

			}

			override fun getFabricId(): Identifier {
				return Identifier(HiiroSakuraClient.modId, "shaders")
			}

		})
	}


}
