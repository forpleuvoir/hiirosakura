package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.nebula.config.ConfigSerializable
import moe.forpleuvoir.nebula.config.container.ConfigContainer

fun ConfigContainer.flat(predicate: (ConfigSerializable) -> Boolean = { true }): Sequence<ConfigSerializable> {
    return sequence {
        configs().forEach {
            if (it is ConfigContainer) {
                yieldAll(it.flat(predicate))
            } else {
                if (predicate(it))
                    yield(it)
            }
        }
    }
}