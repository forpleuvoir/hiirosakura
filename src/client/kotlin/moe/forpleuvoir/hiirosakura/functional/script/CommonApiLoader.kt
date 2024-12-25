package moe.forpleuvoir.hiirosakura.functional.script

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.ModInitializerEvent
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import javax.script.ScriptEngine

@EventSubscriber
object CommonApiLoader {

    private val log = logger()

    @Subscriber
    fun init(event: ModInitializerEvent) {
        if (event.meta.id != HiiroSakura.MOD_ID) return
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
            .registerReloadListener(object : SimpleSynchronousResourceReloadListener {
                override fun getFabricId(): Identifier = identifier("common_api")
                override fun reload(manager: ResourceManager) {
                    commonApi.clear()
                    manager.findResources("script") { it.path.endsWith(".js") }
                        .forEach { (path, resource) ->
                            runCatching {
                                resource.inputStream.use { inputStream ->
                                    val content = inputStream.bufferedReader().use { it.readText() }
                                    commonApi.add(content)
                                    log.info("Loaded resource: $path") // 增加成功日志
                                }
                            }.onFailure { exception ->
                                log.error("Failed to load resource: $path", exception) // 提供具体文件路径和异常信息
                            }
                        }
                }
            })
    }

    private val commonApi = mutableListOf<String>()

    fun eval(engine: ScriptEngine) {
        commonApi.forEach(engine::eval)
    }

}