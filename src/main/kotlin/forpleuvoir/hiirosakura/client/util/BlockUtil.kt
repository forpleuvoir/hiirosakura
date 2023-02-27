package forpleuvoir.hiirosakura.client.util

import forpleuvoir.hiirosakura.client.mixin.MixinState
import net.minecraft.block.BlockState
import net.minecraft.registry.Registries
import java.util.stream.Collectors

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.util

 * 文件名 BlockUtil

 * 创建时间 2022/2/12 3:37

 * @author forpleuvoir

 */
fun BlockState.asString(): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append(Registries.BLOCK.getId(this.block))
    stringBuilder.append(getPropertiesString())
    return stringBuilder.toString()
}

fun BlockState.getPropertiesString(): String {
    val stringBuilder = StringBuilder()
    if (!entries.isEmpty()) {
        stringBuilder.append('{')
        stringBuilder.append(
            entries.entries.stream().map(MixinState.PROPERTY_MAP_PRINTER()).collect(Collectors.joining(","))
        )
        stringBuilder.append('}')

    }
    return stringBuilder.toString()
}

fun BlockState.getPropertiesMap(): Map<String, String> {
    val map = HashMap<String, String>()
    if (!entries.isEmpty()) {
        entries.entries.stream().map(MixinState.PROPERTY_MAP_PRINTER()).forEach {
            val split = it.split("=")
            map[split[0]] = split[1]
        }

    }
    return map
}

fun isContains(blockState: BlockState, list: List<String>): Boolean {
    try {
        list.forEach { str ->
            if (!isContains(blockState, str)) return false
        }
        return true
    } catch (e: Exception) {
        return false
    }
}

fun isContains(blockState: BlockState, str: String): Boolean {
    try {
        val properties =
            str.replace("{", "").replace("}", "").replace(Registries.BLOCK.getId(blockState.block).toString(), "")
        val map = blockState.getPropertiesMap()
        properties.split(",").forEach {
            val split = it.split("=")
            if (map.containsKey(split[0])) {
                if (map[split[0]] != split[1]) {
                    return false
                }
            } else {
                return false
            }
        }
        return true
    } catch (e: Exception) {
        return false
    }
}