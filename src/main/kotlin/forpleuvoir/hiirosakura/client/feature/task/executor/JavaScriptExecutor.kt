package forpleuvoir.hiirosakura.client.feature.task.executor

import forpleuvoir.hiirosakura.client.feature.event.base.Event
import forpleuvoir.hiirosakura.client.feature.task.TimeTask
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IJavaScriptInterface
import forpleuvoir.hiirosakura.client.feature.task.executor.base.JavaScriptInterface
import forpleuvoir.hiirosakura.client.util.HSLogger.Companion.getLogger
import net.minecraft.text.LiteralText
import javax.script.ScriptEngineManager

/**
 * js脚本执行器
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task.executor
 *
 * #class_name JavaScriptExecutor
 *
 * #create_time 2021/7/27 22:34
 */
class JavaScriptExecutor(private val script: String, private val event: Event?) : IExecutor {
	@Transient
	private val engine = ScriptEngineManager().getEngineByName("nashorn")
	private val javaScriptInterface: IJavaScriptInterface = JavaScriptInterface()
	private val include: String = JSHeadFile.getContent()
	override fun execute(task: TimeTask) {
		try {
			engine.eval(include)
			engine.put("\$task", task)
			engine.put("\$log", log)
			engine.put("\$hs", javaScriptInterface)
			event?.let { engine.put("\$event", it) }
			engine.eval(script)
		} catch (e: Exception) {
			task.hs.addChatMessage(LiteralText("§c" + e.message))
			log.error(e)
		}
	}

	override val asString: String
		get() = script

	companion object {
		@Transient
		private val log = getLogger(JavaScriptExecutor::class.java)
	}

}