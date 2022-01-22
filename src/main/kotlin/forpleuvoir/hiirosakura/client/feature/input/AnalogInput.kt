package forpleuvoir.hiirosakura.client.feature.input

import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 模拟输入
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.input
 *
 * 文件名 AnalogInput
 *
 * 创建时间 2021-07-28 11:50
 */
object AnalogInput {
	private val data: MutableList<Node> = LinkedList()

	@JvmStatic
	fun tick() {
		for (node in data) {
			node.tick()
		}
	}

	@JvmStatic
	fun isPress(key: Key): Boolean {
		val isPress = AtomicBoolean(false)
		data.stream().filter { it.key == key }.findFirst()
			.ifPresent { isPress.set(it.isPress) }
		return isPress.get()
	}

	@JvmStatic
	operator fun set(key: Key, value: Int) {
		data.stream().filter { it.key == key }.findFirst().ifPresent { it.set(value) }
	}

	@JvmStatic
	fun setOnReleasedCallBack(key: Key, onReleasedCallBack: (Key) -> Unit) {
		data.stream().filter { it.key == key }.findFirst()
			.ifPresent { it.onReleased = onReleasedCallBack }
	}

	private class Node private constructor(val key: Key) {
		var value = 0
		var onReleased: ((Key) -> Unit)? = null

		fun set(value: Int) {
			if (value > 0) this.value = value
		}

		val isPress: Boolean
			get() = value > 0

		fun tick() {
			if (value > 0) {
				value--
				if (value == 0) {
					onReleased?.invoke(key)
				}
			}
		}

		companion object {
			fun key(key: Key): Node {
				return Node(key)
			}
		}
	}

	enum class Key {
		FORWARD, BACK, LEFT, RIGHT, JUMP, SNEAK, ATTACK, USE, PICK_ITEM
	}

	init {
		for (key in Key.values()) {
			data.add(Node.key(key))
		}
	}
}