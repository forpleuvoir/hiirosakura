package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal

import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject

/**
 * 写时复制辅助：编辑器每次修改都生成新的容器实例，
 * 使 Compose 能通过引用变化自然重组，不需要手动刷新标记。
 */

fun SerializeObject.replaced(key: String, value: SerializeElement): SerializeObject =
    SerializeObject().apply {
        this@replaced.forEach { (k, v) ->
            this[k] = if (k == key) value else v
        }
    }

fun SerializeObject.removed(key: String): SerializeObject =
    SerializeObject().apply {
        this@removed.forEach { (k, v) ->
            if (k != key) this[k] = v
        }
    }

fun SerializeObject.renamed(oldKey: String, newKey: String): SerializeObject =
    SerializeObject().apply {
        this@renamed.forEach { (k, v) ->
            this[if (k == oldKey) newKey else k] = v
        }
    }

fun SerializeObject.plus(key: String, value: SerializeElement): SerializeObject =
    SerializeObject().apply {
        this@plus.forEach { (k, v) ->
            this[k] = v
        }
        this[key] = value
    }

fun SerializeArray.replacedAt(index: Int, value: SerializeElement): SerializeArray =
    SerializeArray().apply {
        this@replacedAt.forEachIndexed { i, v ->
            this.add(if (i == index) value else v)
        }
    }

fun SerializeArray.withoutAt(index: Int): SerializeArray =
    SerializeArray().apply {
        this@withoutAt.forEachIndexed { i, v ->
            if (i != index) this.add(v)
        }
    }

fun SerializeArray.plus(value: SerializeElement): SerializeArray =
    SerializeArray().apply {
        this@plus.forEach { this.add(it) }
        this.add(value)
    }
