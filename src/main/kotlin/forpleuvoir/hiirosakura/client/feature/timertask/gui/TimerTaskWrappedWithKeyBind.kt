package forpleuvoir.hiirosakura.client.feature.timertask.gui

import com.google.gson.JsonElement
import forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor.JsExecutor
import forpleuvoir.ibuki_gourd.keyboard.KeyBind
import forpleuvoir.ibuki_gourd.keyboard.KeyboardUtil

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui

 * 文件名 TimerTaskWrappedWithKeyBind

 * 创建时间 2022/1/19 18:42

 * @author forpleuvoir

 */
class TimerTaskWrappedWithKeyBind : TimerTaskWrapped() {

	fun handlerKey() {
		KeyboardUtil.wasPressed(this.keyBind)
	}

	val keyBind: KeyBind = KeyBind().apply {
		callback = {
			run()
		}
	}

	override val asJsonElement: JsonElement
		get() = super.asJsonElement.apply {
			this.asJsonObject.add("keyBind", keyBind.asJsonElement)
		}

	override fun setValueFromJsonElement(jsonElement: JsonElement) {
		if (jsonElement.isJsonObject) {
			jsonElement.asJsonObject.let {
				timerTask = JsExecutor.fromJson(it["timerTask"].asJsonObject)!!
				keyBind.setValueFromJsonElement(it["keyBind"])
				runAt = RunAt.valueOf(it["runAt"].asString)
			}
		}
	}


}