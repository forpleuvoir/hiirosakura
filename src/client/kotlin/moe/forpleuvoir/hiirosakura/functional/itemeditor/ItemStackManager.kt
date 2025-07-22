package moe.forpleuvoir.hiirosakura.functional.itemeditor

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.ibukigourd.util.loader
import moe.forpleuvoir.nebula.common.util.ioAsync
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeArray
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import moe.forpleuvoir.nebula.serialization.gson.jsonStringToObject
import moe.forpleuvoir.nebula.serialization.gson.toJsonString
import net.minecraft.item.ItemStack
import net.minecraft.registry.DynamicRegistryManager
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.jvm.optionals.getOrNull

object ItemStackManager {

    private val log = logger()

    private const val KEY: String = "item_stack_manager"

    private val dataPath = File(loader.configDir.toFile(), "${HiiroSakura.MOD_ID}/data").toPath()

    private val _items = CopyOnWriteArrayList<ItemStack>()

    private var changed = false

    fun asSequence() = _items.asSequence()

    val lastIndex get() = _items.lastIndex

    fun forEach(action: (ItemStack) -> Unit) {
        _items.forEach(action)
    }

    fun forEachIndexed(action: (index: Int, ItemStack) -> Unit) {
        _items.forEachIndexed(action)
    }

    operator fun set(index: Int, itemStack: ItemStack) {
        _items[index] = itemStack
        changed = true
    }

    fun add(itemStack: ItemStack) {
        _items.add(itemStack)
        changed = true
    }

    fun add(index: Int, itemStack: ItemStack) {
        _items.add(index, itemStack)
        changed = true
    }

    fun moveElement(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        val movingElement = this.removeAt(fromIndex)
        this.add(toIndex, movingElement)
    }

    fun removeAt(index: Int): ItemStack {
        return _items.removeAt(index).apply() {
            changed = true
        }
    }

    fun remove(itemStack: ItemStack) {
        if (_items.remove(itemStack)) {
            changed = true
        }
    }

    fun loadDataAsync(registryManager: DynamicRegistryManager) = ioAsync {
        runCatching {
            ConfigUtil.run {
                val file = configFile("${KEY}.json", dataPath)
                val json = readFileToString(file)
                deserialization(registryManager, json.jsonStringToObject())
            }
        }.onFailure {
//            saveDataAsync(registryManager)
            log.warn(it)
        }
    }

    fun saveDataAsync(registryManager: DynamicRegistryManager) = ioAsync {
        if (changed) {
            runCatching {
                ConfigUtil.run {
                    val file = configFile("${KEY}.json", dataPath)
                    val str = (serialization(registryManager) as SerializeObject).toJsonString()
                    writeToFile(str, file)
                }
            }.onFailure {
                log.warn(it)
            }
            changed = false
            return@ioAsync true
        }
        return@ioAsync false
    }

    fun serialization(registryManager: DynamicRegistryManager): SerializeElement = serializeObject {
        "items" to serializeArray().apply {
            _items.forEach { itemStack ->
                runCatching {
                    ItemStack.CODEC.encodeStart(registryManager.getOps(NebulaOps), itemStack)
                        .resultOrPartial {
                            log.error("serialize item stack error: $it")
                        }
                        .getOrNull()
                        ?.let {
                            add(it)
                        }
                }
            }
        }
    }

    fun deserialization(registryManager: DynamicRegistryManager, serializeElement: SerializeElement) {
        val temp = buildList {
            serializeElement.checkType {
                check<SerializeObject> { obj ->
                    obj["items"]!!.asArray.forEach {
                        ItemStack.CODEC.parse(registryManager.getOps(NebulaOps), it)
                            .resultOrPartial {
                                log.error("deserialize item stack error: $it")
                            }
                            .getOrNull()
                            ?.let { itemStack ->
                                this@buildList.add(itemStack)
                            }
                    }
                }
            }
        }
        _items.clear()
        _items.addAll(temp)
    }

}