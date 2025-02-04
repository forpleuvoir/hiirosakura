package moe.forpleuvoir.hiirosakura.functional.task.executor

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.executor.Executor
import moe.forpleuvoir.hiirosakura.functional.script.CommonApiLoader
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSPlayerEntity
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.client.MinecraftClient
import javax.script.ScriptEngineManager

class ScriptExecutor(
    private val script: String,
    params: Map<String, Any> = mapOf()
) : TaskExecutor<MinecraftClient>, Executor {

    companion object : Deserializer<ScriptExecutor> {

        private val log = ModLogger(ScriptExecutor::class, HiiroSakura.MOD_NAME)

        private val scriptEngine get() = ScriptEngineManager().getEngineByName("nashorn")

        override fun deserialization(serializeElement: SerializeElement): ScriptExecutor {
            return serializeElement.checkType<ScriptExecutor> {
                check<SerializePrimitive> {
                    ScriptExecutor(it.asString)
                }
            }.getOrThrow()
        }

    }

    private val engine = scriptEngine

    init {
        params.forEach(engine::put)
        engine.put("_this", this)
    }

    operator fun set(key: String, value: Any) {
        engine.put(key, value)
    }

    fun putAll(params: Map<String, Any>) {
        params.forEach(engine::put)
    }

    override fun execute(task: TickTask<MinecraftClient>, context: MinecraftClient) {
        catch {
            engine.put("_client", context)
            engine.put("_task", task)
        }
        execute()
    }

    override fun execute() {
        catch {
            CommonApiLoader.eval(engine)
            mc.player?.let {
                engine.put("player", HSPlayerEntity(it))
            }
            engine.eval(TaskManager.Config.scriptCommonLib)
            engine.eval(script)
        }
    }

    private inline fun catch(block: () -> Unit) {
        runCatching {
            block()
        }.onFailure {
            Toast.showToast(
                duration = Toast.LONG_DURATION,
                text = Literal(it.message ?: "unknown").withColor(Colors.RED)
            )
            log.error(it)
        }
    }

    override val asString: String
        get() = script

    override fun serialization(): SerializeElement = SerializePrimitive(script)
}