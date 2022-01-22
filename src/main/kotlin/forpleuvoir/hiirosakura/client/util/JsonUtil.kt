package forpleuvoir.hiirosakura.client.util

import com.google.gson.*

/**
 * Json工具
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.util
 *
 * 文件名 JsonUtil
 *
 * 创建时间 2021/6/14 23:52
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

	fun JsonObject.ifNullOr(key: String, or: String): String {
		return if (this[key] != null) this[key].asString else or
	}

	fun JsonObject.ifNullOr(key: String, or: Number): Number {
		return if (this[key] != null) this[key].asNumber else or
	}

	fun JsonObject.ifNullOr(key: String, or: JsonElement): JsonElement {
		return if (this[key] != null) this[key] else or
	}

	fun Any.toJsonObject(): JsonObject {
		return gson.toJsonTree(this).asJsonObject
	}

	fun Collection<*>.toJsonArray(): JsonArray {
		return gson.toJsonTree(this).asJsonArray
	}
}

