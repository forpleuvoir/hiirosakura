package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeArray
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

interface Matcher<T> : Serializable {

    fun match(obj: T): Boolean

}

interface MatchEntry<T> : Matcher<T>, Serializable {

    val mode: MatchMode

    fun matchWithMode(obj: T): Boolean = mode.handleResult(match(obj))

    enum class MatchMode(private val value: Boolean) : Serializable {
        /**
         * 表示匹配模式中的包含模式，用于确定匹配的项目是否应包含在结果内。
         */
        Include(true),

        /**
         * 表示匹配模式中的排除模式，用于确定匹配的项目是否应从结果中排除。
         */
        Exclude(false);

        companion object : Deserializer<MatchMode> {
            override fun deserialization(serializeElement: SerializeElement): MatchMode {
                return serializeElement.checkType<SerializePrimitive, MatchMode> {
                    if (it.asBoolean) Include else Exclude
                }.getOrThrow()
            }

            fun fromBoolean(value: Boolean): MatchMode = if (value) Include else Exclude
        }

        fun toBoolean(): Boolean = value

        fun handleResult(result: Boolean): Boolean = if (value) result else !result

        override fun serialization(): SerializeElement = SerializePrimitive(value)
    }
}


interface CompositeMatcher<T> : Matcher<T>, Cloneable {

    val entries: List<MatchEntry<T>>

    val mode: MatchMode

    public override fun clone(): CompositeMatcher<T>

    override fun match(obj: T): Boolean {
        return when (mode) {
            MatchMode.AnyMatch  -> entries.any { it.matchWithMode(obj) }
            MatchMode.AllMatch  -> entries.all { it.matchWithMode(obj) }
            MatchMode.NoneMatch -> entries.none { it.matchWithMode(obj) }
        }
    }

    override fun serialization(): SerializeElement =
        serializeObject {
            "mode" to mode
            "entries" to serializeArray(entries)
        }

    enum class MatchMode : Serializable {
        /**
         *
         * 在任意匹配模式下，只要存在一个匹配项符合条件，则整个匹配规则被视为通过。
         * 该模式常用于需要满足至少一个条件即可的场景。
         *
         */
        AnyMatch,

        /**
         * 当且仅当所有匹配项均不符合指定规则时，匹配才成功。
         * 适用于需要确保没有一项符合条件的场景，例如检测某集合中是否不存在特定属性或值。
         */
        NoneMatch,

        /**
         *  此模式通常用于需要确保全部条件均满足的场景。例如，当需要验证某个集合中所有元素都满足给定规则时，可采用此模式。
         */
        AllMatch;

        companion object : Deserializer<MatchMode> {
            override fun deserialization(serializeElement: SerializeElement): MatchMode {
                return MatchMode.entries
                    .find { it.name == serializeElement.asString }
                    ?: throw IllegalArgumentException("Unsupported mode ${serializeElement.asString}")
            }
        }

        override fun serialization(): SerializeElement = SerializePrimitive(name)

    }

}