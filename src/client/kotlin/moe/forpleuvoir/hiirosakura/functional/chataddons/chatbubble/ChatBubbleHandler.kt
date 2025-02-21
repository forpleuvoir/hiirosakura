package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.stringPairList
import moe.forpleuvoir.ibukigourd.config.item.vector2f
import moe.forpleuvoir.ibukigourd.text.McText
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.duration
import moe.forpleuvoir.nebula.config.item.impl.float
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector2f
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object ChatBubbleHandler:ModConfigContainer("chat_bubble") {

    val enabled by boolean("enable", false)

    val onlyYRotation by boolean("only_y_rotation", true)

    val offset by vector2f("offset", Vector2f(0f, 0.0f), Vector2f(-1f, -1f), Vector2f(10f, 10f))

    val scale by vector2f("scale", Vector2f(1f, 1f), Vector2f(0.1f, 0.1f), Vector2f(10f, 10f))

    val maxWidth by float("max_width", 120f, 9f, 480f)

    val textureColor by color("texture_color", Color("#FFFF4646"))

    val textColor by color("text_color", Color("#FFFFFF"))

    val duration by duration("duration", 10.seconds, Duration.ZERO, 60.seconds)

    val fadeInDuration by duration("fade_in_duration", 0.25.seconds, 0.seconds, 2.seconds)

    val fadeOutDuration by duration("fade_out_duration", 0.25.seconds, 0.seconds, 2.seconds)

    val matchMapping by stringPairList(
        "match_mapping", listOf(
            "#single" to "<(?<name>[^>]+)>\\s(?<message>.+)"
        )
    )

    private val bubbleQueue = ConcurrentHashMap<String, ChatBubble>()


    @JvmStatic
    fun addChatBubble(text: McText) {
        if (!enabled) return
        ChatBubble.fromChatMessage(text.string)?.let { bubbleQueue[it.playerName] = it }
    }

    fun render(playerName: String, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider.Immediate, light: Int) {
        bubbleQueue.filter { it.value.shouldRemove }.forEach { bubbleQueue.remove(it.key) }
        bubbleQueue[playerName]?.render(matrices, vertexConsumers, light)
    }

}