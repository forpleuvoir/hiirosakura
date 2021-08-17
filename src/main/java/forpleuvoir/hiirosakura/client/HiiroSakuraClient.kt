package forpleuvoir.hiirosakura.client

import fi.dy.masa.malilib.event.InitializationHandler
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
import java.util.function.Consumer

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
	private val tickers: Queue<Consumer<HiiroSakuraClient>> = ConcurrentLinkedQueue()

	//客户端任务队列
	private val tasks: Queue<Consumer<MinecraftClient>> = ConcurrentLinkedQueue()
	private val analogInput = AnalogInput.getInstance()
	var tickCounter: Long = 0
		private set

	override fun onInitializeClient() {
		InitializationHandler.getInstance().registerInitializationHandler(InitHandler())
		//MinecraftClient.tick注册
		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient -> onEndTick(client) })
	}

	/**
	 * 添加游戏信息
	 *
	 * @param message 消息文本
	 */
	fun showMessage(message: Text?) {
		mc.inGameHud.addChatMessage(MessageType.GAME_INFO, message, Util.NIL_UUID)
	}

	fun addChatMessage(message: Text?) {
		mc.inGameHud.addChatMessage(MessageType.SYSTEM, message, Util.NIL_UUID)
	}

	/**
	 * 客户端tick之后会调用
	 *
	 * @param client [MinecraftClient]
	 */
	private fun onEndTick(client: MinecraftClient) {
		analogInput.tick()
		tickers.forEach(Consumer { minecraftClientConsumer: Consumer<HiiroSakuraClient> ->
			minecraftClientConsumer.accept(
				this
			)
		})
		runTask(client)
		tickCounter++
	}

	/**
	 * 执行所有未被执行的客户端任务
	 *
	 * @param client [MinecraftClient]
	 */
	private fun runTask(client: MinecraftClient) {
		val iterator = tasks.iterator()
		while (iterator.hasNext()) {
			val next = iterator.next()
			next.accept(client)
			iterator.remove()
		}
	}

	/**
	 * 添加客户端任务
	 * 客户端每次tick结束后会执行，执行后的任务会被删除
	 *
	 * @param task [<]
	 */
	fun addTask(task: Consumer<MinecraftClient>) {
		tasks.add(task)
	}

	/**
	 * 添加Tick处理程序
	 *
	 * 每次客户端tick结束都会执行一次
	 *
	 * @param handler [<]
	 */
	fun addTickHandler(handler: Consumer<HiiroSakuraClient>) {
		tickers.add(handler)
	}

	fun sendMessage(message: String?) {
		Objects.requireNonNull(mc.player)?.sendChatMessage(message)
	}
}