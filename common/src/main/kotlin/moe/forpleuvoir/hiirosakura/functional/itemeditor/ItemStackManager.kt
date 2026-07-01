package moe.forpleuvoir.hiirosakura.functional.itemeditor

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.nebula.common.util.ioAsync
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.common.util.requireTypeOrNull
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.serialization.json.JsonDialect
import net.minecraft.core.RegistryAccess
import net.minecraft.world.item.ItemStack
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

object ItemStackManager {

    private val log = logger()

    private const val KEY: String = "item_stack_manager"

    private val dataPath = File(PLATFORM.getConfigDir(), "${HiiroSakura.MOD_ID}/data").toPath()

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

    fun loadDataAsync(registryAccess: RegistryAccess) = ioAsync {
        runCatching {
            ConfigUtil.run {
                val file = configFile("${KEY}.json", dataPath)
                val json = readFileToString(file)
                deserialization(registryAccess, JsonDialect.decode(json).getOrThrow())
            }
        }.onFailure {
//            saveDataAsync(registryManager)
            log.warn(it)
        }
    }

    fun saveDataAsync(registryAccess: RegistryAccess) = ioAsync {
        if (changed) {
            runCatching {
                ConfigUtil.run {
                    val file = configFile("${KEY}.json", dataPath)
                    writeToFile(JsonDialect.encode(serialization(registryAccess)), file)
                }
            }.onFailure {
                log.warn(it)
            }
            changed = false
            return@ioAsync true
        }
        return@ioAsync false
    }

    fun serialization(registryAccess: RegistryAccess): SerializeElement = SerializeObject.build {
        "items" arr {
            _items.forEach { itemStack ->
                runCatching {
                    ItemStack.CODEC.encodeStart(registryAccess.createSerializationContext(NebulaOps), itemStack)
                        .orThrow
                        .let {
                            add(it)
                        }
                }.onFailure {
                    log.error("serialize item stack error: $it")
                    log.error(it)
                }
            }
        }
    }

    fun deserialization(registryAccess: RegistryAccess, serializeElement: SerializeElement) {
        val temp = buildList {
            serializeElement.requireTypeOrNull<SerializeObject>()?.let { obj ->
                obj.requireKey("items").requireType<SerializeArray>().forEach {
                    runCatching {
                        ItemStack.CODEC.parse(registryAccess.createSerializationContext(NebulaOps), it)
                            .orThrow
                            .let { itemStack ->
                                this@buildList.add(itemStack)
                            }
                    }.onFailure { throwable ->
                        log.error("deserialize item stack error: $throwable")
                        log.error(throwable)
                    }
                }
            }
        }
        _items.clear()
        _items.addAll(temp)
    }

}