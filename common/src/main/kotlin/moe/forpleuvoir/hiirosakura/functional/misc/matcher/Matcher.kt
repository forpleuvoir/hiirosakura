package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.enum

interface Matcher<T> {

    fun match(obj: T): Boolean

}

interface MatchEntry<T> : Matcher<T> {

    val mode: MatchMode

    fun matchWithMode(obj: T): Boolean = mode.handleResult(match(obj))

    enum class MatchMode(private val value: Boolean) {
        /**
         * 表示匹配模式中的包含模式，用于确定匹配的项目是否应包含在结果内。
         */
        Include(true),

        /**
         * 表示匹配模式中的排除模式，用于确定匹配的项目是否应从结果中排除。
         */
        Exclude(false);

        companion object : Codec<MatchMode> by Codec.enum() {

            fun fromBoolean(value: Boolean): MatchMode = if (value) Include else Exclude
        }

        fun toBoolean(): Boolean = value

        fun handleResult(result: Boolean): Boolean = if (value) result else !result

    }
}


interface CompositeMatcher<T> : Matcher<T> {

    val entries: Iterable<MatchEntry<T>>

    val mode: MatchMode

    override fun match(obj: T): Boolean {
        return when (mode) {
            MatchMode.AnyMatch  -> entries.any { it.matchWithMode(obj) }
            MatchMode.AllMatch  -> entries.all { it.matchWithMode(obj) }
            MatchMode.NoneMatch -> entries.none { it.matchWithMode(obj) }
        }
    }

    enum class MatchMode {
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

        companion object : Codec<MatchMode> by Codec.enum()

    }

}