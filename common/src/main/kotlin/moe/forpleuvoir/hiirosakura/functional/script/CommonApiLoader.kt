package moe.forpleuvoir.hiirosakura.functional.script

import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.hiirosakura.util.resourceLocation
import moe.forpleuvoir.ibukigourd.util.SimpleResourceReloaderListener
import net.minecraft.server.packs.resources.PreparableReloadListener
import javax.script.ScriptEngine

object CommonApiLoader : SimpleResourceReloaderListener<List<String>>() {

    val RESOURCE_ID = resourceLocation("common_api")

    private val log = logger()

    override fun prepare(sharedState: PreparableReloadListener.SharedState): List<String> {
        return buildList {
            sharedState.resourceManager()
                .listResources("script") { it.path.endsWith(".js") }
                .forEach { (path, resource) ->
                    runCatching {
                        resource.open().use { inputStream ->
                            add(inputStream.bufferedReader().use { it.readText() })
                            log.info("Loaded resource: $path")
                        }
                    }.onFailure { exception ->
                        log.error("Failed to load resource: $path", exception)
                    }
                }
        }
    }

    override fun apply(prepared: List<String>, sharedState: PreparableReloadListener.SharedState) {
        commonApi.clear()
        prepared.forEach(commonApi::add)
    }

    private val commonApi = mutableListOf<String>()

    fun eval(engine: ScriptEngine) {
        commonApi.forEach(engine::eval)
    }

}