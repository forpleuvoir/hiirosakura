package forpleuvoir.hiirosakura.client.util

import com.google.gson.*

/**
 * Json工具
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.util
 *
 * #class_name JsonUtil
 *
 * #create_time 2021/6/14 23:52
 */
object JsonUtil {

	val gson: Gson = GsonBuilder().setPrettyPrinting().create()

	fun toStringBuffer(obj: Any?): StringBuffer {
		return StringBuffer(gson.toJson(obj))
	}

	/**
	 * 将对象转换成json字符串
	 *
	 * @param json 需要转换的对象
	 * @return json字符串
	 */
	fun toJsonStr(json: Any?): String {
		return gson.toJson(json)
	}

	/**
	 * 将json字符串转换成JsonObject
	 *
	 * @param json 需要转换的字符串对象
	 * @return JsonObject对象
	 */
	fun parseToJsonObject(json: String?): JsonObject {
		return JsonParser.parseString(json).asJsonObject
	}

	fun parseToJsonArray(json: String): JsonArray {
		return JsonParser.parseString(json).asJsonArray
	}
}

fun Any.toJsonObject(): JsonObject {
	return JsonUtil.gson.toJsonTree(this).asJsonObject
}