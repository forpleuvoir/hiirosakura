package moe.forpleuvoir.hiirosakura.functional.task.executor

import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.util.sendMessage
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import net.minecraft.client.MinecraftClient

data class MessageExecutor(
    val message: String
) : TaskExecutor<MinecraftClient> {
    override fun execute(task: TickTask<MinecraftClient>, client: MinecraftClient) {
        client.sendMessage(message)
    }

    override val asString: String
        get() = message

    override fun serialization(): SerializeElement = SerializePrimitive(message)
}