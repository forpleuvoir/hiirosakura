package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.util.renameKey
import moe.forpleuvoir.nebula.common.api.ExperimentalApi
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import moe.forpleuvoir.nebula.serialization.gson.jsonStringToObject
import moe.forpleuvoir.nebula.serialization.json.JsonSerializer.Companion.dumpAsJson
import java.io.File


@EventSubscriber
object CustomRadialMenuManager {

    private const val KEY = "custom_radial_menu"

    private val logger = logger()

    internal var editing = false

    private val path = File(PLATFORM.getConfigDir(), "${HiiroSakura.MOD_ID}/$KEY")

    val customRadialMenus: LinkedHashMap<String, CustomRadialMenu> = LinkedHashMap()

    fun rename(oldName: String, newName: String) {
        customRadialMenus.renameKey(oldName, newName)
        customRadialMenus[newName]?.shortcuts?.name(InlineStyleText(newName))
    }

    @Subscriber
    fun init(@Suppress("unused") event: ClientLifecycleEvent.ClientStartingEvent) {
        runBlocking {
            logger.info("Loading Custom Radial Menu...")
            asyncLoad()

        }
    }

    @Subscriber
    fun onSave(@Suppress("unused") event: ClientLifecycleEvent.ClientStopEvent) {
        runBlocking {
            asyncSave()
        }
    }

    suspend fun asyncLoad() {
        runCatching {
            if (!path.exists()) {
                path.mkdirs()
                return
            }
            customRadialMenus.forEach { (_, menu) ->
                menu.onUnload()
            }
            customRadialMenus.clear()
            path.listFiles { file ->
                file.name.endsWith(".json")
            }.forEach { file ->
                val name = file.name.removeSuffix(".json")
                runCatching {
                    logger.info("Loading $name...")
                    customRadialMenus[name] = ConfigUtil.readFileToString(file)
                        .jsonStringToObject()
                        .let {
                            CustomRadialMenu.deserialization(it)
                        }.apply {
                            shortcuts.name(InlineStyleText(name))
                            onLoad()
                        }
                }.onFailure {
                    logger.warn(it)
                }
            }
        }.onFailure {
            logger.warn(it)
        }
    }

    @OptIn(ExperimentalApi::class)
    suspend fun asyncSave() {
        if (editing) return
        runCatching {
            //删除所有json文件
            path.listFiles { file ->
                file.name.endsWith(".json")
            }.forEach { file ->
                try {
                    if (file.exists() && !file.delete()) {
                        logger.warn("Failed to delete file: ${file.absolutePath}")
                    }
                } catch (e: Exception) {
                    logger.warn("Error deleting file: ${file.absolutePath}", e)
                }
            }
            //保存新的配置文件
            customRadialMenus.forEach { (key, menu) ->
                ConfigUtil.writeToFile(menu.serialization().dumpAsJson(true), File(path, "$key.json"))
            }
        }.onFailure {
            logger.warn(it)
        }
    }


}