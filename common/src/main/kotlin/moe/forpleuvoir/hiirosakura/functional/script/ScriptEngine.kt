package moe.forpleuvoir.hiirosakura.functional.script

import org.apache.commons.jexl3.*
import org.apache.commons.jexl3.introspection.JexlPermissions
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

class ScriptEngine(
    nameSpace: Map<String, Any>,
    cache: Int = 1024,
    strict: Boolean = true,
    silent: Boolean = false,
    antish: Boolean = false,
    permissions: JexlPermissions = JexlPermissions.UNRESTRICTED,
    classLoader: ClassLoader = ScriptEngine::class.java.classLoader
) {

    private val jexl = JexlBuilder()
        .cache(cache)
        .arithmetic(RecordJexlArithmetic(strict))
        .loader(classLoader)
        .strict(strict)
        .silent(silent)
        .antish(antish)
        .permissions(permissions)
        .namespaces(nameSpace)
        .create()

    private val scriptCache = ConcurrentHashMap<String, JexlScript>(10)

    private val removeTag = ConcurrentLinkedDeque<String>()

    fun validateJexl(expression: String): JexlException? {
        runCatching {
            jexl.createScript(expression)
        }.onFailure {
            if(it is JexlException) {
               return it
            }
        }
        return null
    }

    fun eval(code: String, context: MapContext): Any? {
        if (code.isBlank()) return null
        return cache(code).execute(context)
    }

    fun cache(code: String): JexlScript {
        val result = scriptCache.getOrPut(code) { jexl.createScript(code) }
        removeTag.add(code)
        if (removeTag.size > 1000) {
            removeTag.removeFirst()
        }
        return result
    }

    fun remove(code: String) {
        removeTag.remove(code)
        scriptCache.remove(code)
    }

    fun clearCache() {
        removeTag.clear()
        scriptCache.clear()
    }

}
class RecordJexlArithmetic(
    strict: Boolean = true,
) : JexlArithmetic(strict) {

    private val accessors = object : ClassValue<Map<String, Method>>() {
        override fun computeValue(type: Class<*>): Map<String, Method> =
            type.recordComponents.associate { it.name to it.accessor }
    }

    fun propertyGet(record: Record, property: String): Any? {
        val accessor = accessors.get(record.javaClass)[property]
            ?: return JexlEngine.TRY_FAILED

        return accessor.invoke(record)
    }
}