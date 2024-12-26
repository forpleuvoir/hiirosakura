package moe.forpleuvoir.hiirosakura.functional.task.executor

import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.util.sendMessage
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import net.minecraft.client.MinecraftClient

data class CommandExecutor(
    val command: String
) : TaskExecutor<MinecraftClient> {

    override fun execute(task: TickTask<MinecraftClient>, client: MinecraftClient) {
        if (command.startsWith("/")) {
            client.sendMessage(command)
        } else {
            client.sendMessage("/$command")
        }
    }

    override val asString: String
        get() = command

    override fun serialization(): SerializeElement = SerializePrimitive(command)

}