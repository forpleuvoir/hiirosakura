package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.hiirosakura.lang.CustomRadialMenuLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.nebula.common.api.Initializable
import moe.forpleuvoir.nebula.common.util.ioLaunch
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import moe.forpleuvoir.nebula.serialization.json.JsonDialect
import net.minecraft.network.chat.Component
import java.io.File


object CustomRadialMenuManager : Initializable {

    private const val KEY = "custom_radial_menu"

    private val logger = logger()

    internal var editing = false

    private val path = File(PLATFORM.getConfigDir(), "${HiiroSakura.MOD_ID}/$KEY")

    val customRadialMenus = mutableStateMapOf<String, CustomRadialMenu>()

    override fun init() {
        ClientLifecycleEvent.Starting.register { load() }
        ClientLifecycleEvent.Stopping.register { save() }
        ClientLifecycleEvent.OpenGameMenu.register { ioLaunch { asyncSave() } }
    }

    // --- 名称校验 ---

    private fun normalizeName(name: String): String = name.trim()

    private fun isValidFileName(name: String): Boolean {
        if (name.isEmpty()) return false
        val invalidChars = setOf('\\', '/', ':', '*', '?', '"', '<', '>', '|')
        return name.none { it in invalidChars } && name != "." && name != ".."
    }

    fun validateMenuName(name: String): Component? {
        val normalized = normalizeName(name)
        if (normalized.isEmpty()) return CustomRadialMenuLang.nameEmpty
        if (!isValidFileName(normalized)) return CustomRadialMenuLang.nameInvalid
        if (normalized in customRadialMenus) return CustomRadialMenuLang.exists(normalized)
        return null
    }

    // --- 增删改 API ---

    fun add(name: String, menu: CustomRadialMenu): Result<Unit> = runCatching {
        val normalized = normalizeName(name)
        require(normalized.isNotEmpty()) { "Name cannot be empty" }
        require(isValidFileName(normalized)) { "Name contains invalid characters" }
        require(normalized !in customRadialMenus) { "Menu [$normalized] already exists" }
        customRadialMenus[normalized] = menu
        menu.shortcuts.name = InlineStyleText(normalized)
    }

    fun rename(oldName: String, newName: String): Result<Unit> = runCatching {
        val normalizedNew = normalizeName(newName)
        val normalizedOld = normalizeName(oldName)
        if (normalizedOld == normalizedNew) return@runCatching
        require(normalizedNew.isNotEmpty()) { "Name cannot be empty" }
        require(isValidFileName(normalizedNew)) { "Name contains invalid characters" }
        require(normalizedNew !in customRadialMenus) { "Menu [$normalizedNew] already exists" }
        val menu = customRadialMenus.remove(normalizedOld)
            ?: throw NoSuchElementException("Menu [$normalizedOld] not found")
        customRadialMenus[normalizedNew] = menu
        menu.shortcuts.name = InlineStyleText(normalizedNew)
        val oldFile = File(path, "$normalizedOld.json")
        if (oldFile.exists() && !oldFile.delete()) {
            logger.warn("Failed to delete old file: ${oldFile.absolutePath}")
        }
    }

    fun remove(name: String): Result<CustomRadialMenu> = runCatching {
        val normalized = normalizeName(name)
        val menu = customRadialMenus.remove(normalized)
            ?: throw NoSuchElementException("Menu [$normalized] not found")
        menu.unload()
        val file = File(path, "$normalized.json")
        if (file.exists() && !file.delete()) {
            logger.warn("Failed to delete file: ${file.absolutePath}")
        }
        menu
    }

    fun updateSetting(name: String, setting: RadialMenuSetting): Result<Unit> = runCatching {
        val normalized = normalizeName(name)
        val menu = customRadialMenus[normalized]
            ?: throw NoSuchElementException("Menu [$normalized] not found")
        menu.setting = setting
    }

    // --- 加载/保存 ---

    fun load() {
        runBlocking {
            logger.info("Loading Custom Radial Menu...")
            asyncLoad()
        }
    }

    fun save() {
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
                menu.unload()
            }
            customRadialMenus.clear()
            path.listFiles { file ->
                file.name.endsWith(".json")
            }!!.forEach { file ->
                val name = file.name.removeSuffix(".json")
                runCatching {
                    logger.info("Loading $name...")
                    JsonDialect.decode(ConfigUtil.readFileToString(file))
                        .let { CustomRadialMenu.deserialization(it.getOrThrow()).getOrThrow() }
                        .apply {
                            shortcuts.name = InlineStyleText(name)
                            this.load()
                        }
                        .also { menu -> add(name, menu).onFailure { logger.warn("Failed to add loaded menu [$name]: ${it.message}") } }
                }.onFailure {
                    logger.warn(it)
                }
            }
        }.onFailure {
            logger.warn(it)
        }
    }

    suspend fun asyncSave() {
        if (editing) return
        runCatching {
            path.listFiles { file ->
                file.name.endsWith(".json")
            }!!.forEach { file ->
                try {
                    if (file.exists() && !file.delete()) {
                        logger.warn("Failed to delete file: ${file.absolutePath}")
                    }
                } catch (e: Exception) {
                    logger.warn("Error deleting file: ${file.absolutePath}", e)
                }
            }
            customRadialMenus.forEach { (key, menu) ->
                ConfigUtil.writeToFile(JsonDialect.encode(CustomRadialMenu.serialization(menu)), File(path, "$key.json"))
            }
        }.onFailure {
            logger.warn(it)
        }
    }

}