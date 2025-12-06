package moe.forpleuvoir.hiirosakura.functional.task.executor

import moe.forpleuvoir.hiirosakura.functional.executor.Executor
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.sendMessage
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import net.minecraft.client.Minecraft

data class CommandExecutor(
    val command: String
) : TaskExecutor<Minecraft>, Executor {

    override fun execute(task: TickTask<Minecraft>, context: Minecraft) =
        execute()
    override fun asString(): String {
        return command
    }

    override fun serialization(): SerializeElement = SerializePrimitive(command)

    override fun execute() {
        if (command.startsWith("/")) {
            mc.sendMessage(command)
        } else {
            mc.sendMessage("/$command")
        }
    }

}