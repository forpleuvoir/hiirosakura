package moe.forpleuvoir.hiirosakura.functional.itemeditor

import androidx.compose.runtime.mutableStateListOf
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.ibukigourd.util.moveElement
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

object ItemStackManager {

    private val log = logger()

    private const val KEY: String = "item_stack_manager"

    private val dataPath = File(PLATFORM.getConfigDir(), "${HiiroSakura.MOD_ID}/data").toPath()

    private var nextKey = 0L

    val items: List<Keyed<ItemStack>>
        field = mutableStateListOf()

    private var changed = false

    private val mutex = Mutex()

    operator fun set(index: Int, itemStack: ItemStack) {
        items[index] = Keyed(nextKey++, itemStack)
        changed = true
    }

    fun add(itemStack: ItemStack) {
        items.add(Keyed(nextKey++, itemStack))
        changed = true
    }

    fun moveElement(fromIndex: Int, toIndex: Int) {
        items.moveElement(fromIndex, toIndex)
    }

    fun removeAt(index: Int): Keyed<ItemStack> {
        return items.removeAt(index).apply {
            changed = true
        }
    }

    fun remove(itemStack: ItemStack) {
        if (items.removeAll { it.value == itemStack }) {
            changed = true
        }
    }

    fun loadDataAsync(registryAccess: RegistryAccess) = ioAsync {
        mutex.withLock {
            runCatching {
                ConfigUtil.run {
                    val file = configFile("${KEY}.json", dataPath)
                    val json = readFileToString(file)
                    deserialization(registryAccess, JsonDialect.decode(json).getOrThrow())
                }
            }.onFailure {
                log.warn(it)
            }
        }
    }

    fun saveDataAsync(registryAccess: RegistryAccess) = ioAsync {
        mutex.withLock {
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
                return@withLock true
            }
            return@withLock false
        }
    }

    fun serialization(registryAccess: RegistryAccess): SerializeElement = SerializeObject.build {
        "items" arr {
            items.forEach { itemStack ->
                runCatching {
                    ItemStack.CODEC.encodeStart(registryAccess.createSerializationContext(NebulaOps), itemStack.value)
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
        items.clear()
        temp.forEach {
            add(it)
        }
    }

}