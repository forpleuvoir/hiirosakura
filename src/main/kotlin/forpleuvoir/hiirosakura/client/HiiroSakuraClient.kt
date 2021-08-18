package forpleuvoir.hiirosakura.client

import forpleuvoir.hiirosakura.client.event.InitHandler
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.network.MessageType
import net.minecraft.text.Text
import net.minecraft.util.Util
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client
 *
 * #class_name HiiroSakuraClient
 *
 * #create_time 2021/6/10 21:38
 */
@Environment(EnvType.CLIENT)
object HiiroSakuraClient : ClientModInitializer {

	const val MOD_ID = "hiirosakura"
	const val MOD_NAME = "Hiiro Sakura"

	@JvmField
	val mc: MinecraftClient = MinecraftClient.getInstance()

	//客户端tick处理器
	private val tickers: Queue<(HiiroSakuraClient) -> Unit> = ConcurrentLinkedQueue()

	//客户端任务队列
	private val tasks: Queue<(HiiroSakuraClient) -> Unit> = ConcurrentLinkedQueue()

	private val analogInput = AnalogInput

	var tickCounter: Long = 0
		private set

	/**
	 * Mod初始化
	 */
	override fun onInitializeClient() {
		InitHandler.initialize()
		//MinecraftClient.tick注册
		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { onEndTick() })
	}

	/**
	 * 添加游戏信息
	 *
	 * @param message 消息文本
	 */
	fun showMessage(message: Text) {
		mc.inGameHud.addChatMessage(MessageType.GAME_INFO, message, Util.NIL_UUID)
	}

	/**
	 * 添加系统消息
	 * @param message Text 消息文本
	 */
	fun addChatMessage(message: Text) {
		mc.inGameHud.addChatMessage(MessageType.SYSTEM, message, Util.NIL_UUID)
	}

	/**
	 * 发送聊天消息
	 * @param message String
	 */
	fun sendMessage(message: String) {
		mc.player?.sendChatMessage(message)
	}


	/**
	 * 客户端tick之后会调用
	 *
	 */
	private fun onEndTick() {
		analogInput.tick()
		tickers.forEach { it.invoke(this) }
		runTask()
		tickCounter++
	}

	/**
	 * 执行所有未被执行的客户端任务
	 *
	 */
	private fun runTask() {
		val iterator = tasks.iterator()
		while (iterator.hasNext()) {
			val next = iterator.next()
			next.invoke(this)
			iterator.remove()
		}
	}

	/**
	 * 添加客户端任务
	 * 客户端每次tick结束后会执行，执行后的任务会被删除
	 *
	 * @param task 任务
	 */
	fun addTask(task: (HiiroSakuraClient) -> Unit) {
		tasks.add(task)
	}

	/**
	 * 添加Tick处理程序
	 *
	 * 每次客户端tick结束都会执行一次
	 *
	 * @param handler 处理器
	 */
	fun addTickHandler(handler: (HiiroSakuraClient) -> Unit) {
		tickers.add(handler)
	}


}