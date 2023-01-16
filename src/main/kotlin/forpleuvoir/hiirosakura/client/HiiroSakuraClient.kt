package forpleuvoir.hiirosakura.client

import forpleuvoir.hiirosakura.client.initialize.HiiroSakuraInitialize
import forpleuvoir.ibuki_gourd.common.ModInfo
import forpleuvoir.ibuki_gourd.utils.Message
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

/**
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client
 *
 * 文件名 HiiroSakuraClient
 *
 * 创建时间 2021/6/10 21:38
 *
 *  @author forpleuvoir
 */
@Environment(EnvType.CLIENT)
object HiiroSakuraClient : ClientModInitializer, ModInfo {

	override val modId: String
		get() = "hiirosakura"
	override val modName: String
		get() = "Hiiro Sakura"


	@JvmField
	val mc: MinecraftClient = MinecraftClient.getInstance()

	var tickCounter: Long = 0
		private set

	/**
	 * Mod初始化
	 */
	override fun onInitializeClient() {
		HiiroSakuraInitialize.initialize()
	}

	fun endTick() {
		tickCounter++
	}

	/**
	 * 添加游戏信息
	 *
	 * @param message 消息文本
	 */
	fun showMessage(message: Text) {
		Message.showInfo(message)
	}

	/**
	 * 添加系统消息
	 * @param message Text 消息文本
	 */
	fun addChatMessage(message: Text) {
		Message.showChatMessage(message)
	}

	/**
	 * 发送聊天消息
	 * @param message String
	 */
	fun sendMessage(message: String) {
		mc.player?.let {
			if (message.startsWith("/"))
				it.sendCommand(message.substring(1))
			else
				it.sendChatMessage(message, null)
		}

	}

}