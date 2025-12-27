package moe.forpleuvoir.hiirosakura.functional.script

import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.util.SimpleResourceReloaderListener
import net.minecraft.server.packs.resources.PreparableReloadListener
import org.apache.commons.jexl3.MapContext

object CommonScriptLoader : SimpleResourceReloaderListener<List<String>>() {

    val RESOURCE_ID = identifier("common_script")

    private val log = logger()

    override fun prepare(sharedState: PreparableReloadListener.SharedState): List<String> {
        return buildList {
            sharedState.resourceManager()
                .listResources("script") { it.path.endsWith(".jexl") }
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

    private val commonScripts = mutableListOf<String>()

    override fun apply(prepared: List<String>, sharedState: PreparableReloadListener.SharedState) {
        commonScripts.forEach {
            ScriptExecutor.scriptEngine.remove(it)
        }
        commonScripts.clear()
        prepared.filter { it.isNotBlank() }.forEach {
            commonScripts.add(it)
            ScriptExecutor.scriptEngine.cache(it)
        }
    }

    fun eval(engine: ScriptEngine, context: MapContext) {
        commonScripts.forEach {
            engine.eval(it, context)
        }
    }

}