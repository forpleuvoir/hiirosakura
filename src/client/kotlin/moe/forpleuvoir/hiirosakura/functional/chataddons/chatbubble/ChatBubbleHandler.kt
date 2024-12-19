package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.stringPairList
import moe.forpleuvoir.ibukigourd.config.item.vector2f
import moe.forpleuvoir.ibukigourd.text.McText
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.duration
import moe.forpleuvoir.nebula.config.item.impl.float
import moe.forpleuvoir.nebula.config.item.impl.int
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector2f
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object ChatBubbleHandler {

    object Config : ModConfigContainer("chat_bubble") {

        val enabled by keyBindBoolean("enable", false)

        val offset by vector2f("offset", Vector2f(0f, 0.75f), Vector2f(0f, 0f), Vector2f(10f, 10f))

        val textureColor by color("texture_color", Color("#FFFF4646"))

        val textColor by color("text_color", Color("#FFFFFF"))

        val maxWidth by int("max_width", 96, 9, 480)

        val duration by duration("duration", 10.seconds, Duration.ZERO, 60.seconds)

        val scale by vector2f("scale", Vector2f(1f, 1f), Vector2f(0.1f, 0.1f), Vector2f(10f, 10f))

        val backgroundZOffset by float("background_z_offset", 0f, -10f, 10f)

        val onlyYRotation by keyBindBoolean("only_y_rotation", true)

        val matchMapping by stringPairList(
            "match_mapping", listOf(
                "#single" to "<(?<name>[^>]+)>\\s(?<message>.+)"
            )
        )

    }

    private val bubbleQueue = ConcurrentHashMap<String, ChatBubble>()


    @JvmStatic
    fun addChatBubble(text: McText) {
        if (!Config.enabled.value) return
        ChatBubble.fromChatMessage(text.string)?.let { bubbleQueue[it.playerName] = it }
    }

    fun render(playerName: String, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider.Immediate, light: Int) {
        bubbleQueue.filter { it.value.shouldRemove }.forEach { bubbleQueue.remove(it.key) }
        bubbleQueue[playerName]?.render(matrices, vertexConsumers, light)
    }

}