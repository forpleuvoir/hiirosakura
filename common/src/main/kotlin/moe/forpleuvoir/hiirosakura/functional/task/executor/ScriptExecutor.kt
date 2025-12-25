package moe.forpleuvoir.hiirosakura.functional.task.executor

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.executor.Executor
import moe.forpleuvoir.hiirosakura.functional.script.CommonApi
import moe.forpleuvoir.hiirosakura.functional.script.CommonApiLoader
import moe.forpleuvoir.hiirosakura.functional.script.ScriptEngine
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSEntity
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.withColor
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.client.Minecraft
import org.apache.commons.jexl3.MapContext
import org.apache.commons.jexl3.introspection.JexlPermissions

class ScriptExecutor(
    private val script: String,
    context: MutableMap<String, Any> = mutableMapOf()
) : TaskExecutor<Minecraft>, Executor {

    companion object : Deserializer<ScriptExecutor> {

        private val log = ModLogger(ScriptExecutor::class, HiiroSakura.MOD_NAME)

        private val scriptEngine: ScriptEngine by lazy {
            ScriptEngine(
                mapOf(
                    "client" to mc,
                    "common" to CommonApi
                ),
                permissions = JexlPermissions.parse(
                    "java.lang.*",
                    "java.util.*",
                    "moe.forpleuvoir.*"
                )
            )
        }

        override fun deserialization(serializeElement: SerializeElement): ScriptExecutor {
            return serializeElement.checkType {
                check<SerializePrimitive> {
                    ScriptExecutor(it.asString)
                }
            }.getOrThrow()
        }

    }

    private val engine get() = scriptEngine

    private val context = MapContext(context)

    init {
        this.context.set("this", this)
    }

    operator fun set(key: String, value: Any) {
        this.context.set(key, value)
    }

    fun putAll(params: Map<String, Any>) {
        params.forEach(this::set)
    }

    override fun execute(task: TickTask<Minecraft>, context: Minecraft) {
        catch {
            this.context.set("task", task)
        }
        execute()
    }

    override fun execute() {
        catch {
            CommonApiLoader.eval(engine, context)
            mc.player?.let {
                context.set("player", HSEntity.fromEntity(it))
            }
            engine.eval(TaskManager.Config.scriptCommonLib, context)
            engine.eval(script, context)
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

    override fun asString(): String {
        return script
    }

    override fun serialization(): SerializeElement = SerializePrimitive(script)
}