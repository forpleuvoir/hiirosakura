package forpleuvoir.hiirosakura.client.feature.common.javascript

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.feature.common.Runnable
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import forpleuvoir.ibuki_gourd.utils.text
import javax.script.ScriptEngineManager

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.common.javascript

 * 文件名 JsRunner

 * 创建时间 2022/1/21 15:28

 * @author forpleuvoir

 */
open class JsRunner(protected var script: String, protected open val params: MutableMap<String, Any> = HashMap()) : Runnable {

	private val log = HSLogger.getLogger(this.javaClass)
	private val engine = ScriptEngineManager().getEngineByName("nashorn")

	private constructor() : this("")

	override fun run(params: Map<String, Any>) {
		try {
			HeadFile.eval(engine)
			params.forEach(engine::put)
			this.params.forEach(engine::put)
			engine.put("_this", this)
			engine.eval(script)
		} catch (e: Exception) {
			HiiroSakuraClient.addChatMessage("§c${e.message}".text)
			log.error(e)
		}
	}

	operator fun set(key: String, value: Any) {
		params[key] = value
	}

	fun putAll(params: Map<String, Any>) {
		this.params.putAll(params)
	}

	override val asJsonElement: JsonElement
		get() = JsonPrimitive(script)

	override val asString: String
		get() = script

	override fun setValueFromJsonElement(jsonElement: JsonElement) {
		try {
			if (jsonElement.isJsonPrimitive) {
				this.script = jsonElement.asString
			} else {
				log.warn(IbukiGourdLang.SetFromJsonFailed.tString("JsRunner", jsonElement))
			}
		} catch (e: Exception) {
			log.warn(IbukiGourdLang.SetFromJsonFailed.tString("JsRunner", jsonElement))
			log.error(e)
		}
	}

	companion object {
		@JvmStatic
		fun fromJson(json: JsonElement): JsRunner {
			return JsRunner().apply { setValueFromJsonElement(json) }
		}
	}

}