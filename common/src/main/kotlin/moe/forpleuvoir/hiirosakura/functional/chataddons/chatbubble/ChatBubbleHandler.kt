package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.config.items.stringChatBubbleServerConfigMap
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.EntityMatcher
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.vector2f
import moe.forpleuvoir.ibukigourd.config.userdata.setGuiWrapper
import moe.forpleuvoir.ibukigourd.text.McText
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.duration
import moe.forpleuvoir.nebula.config.item.impl.float
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import org.joml.Vector2f
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object ChatBubbleHandler : ModConfigContainer("chat_bubble") {

    val enabled by boolean("enable", false)

    val onlyYRotation by boolean("only_y_rotation", true)

    val offset by vector2f("offset", Vector2f(0f, 0.0f), Vector2f(-10f, -1f), Vector2f(10f, 10f))

    val scale by vector2f("scale", Vector2f(1f, 1f), Vector2f(0.1f, 0.1f), Vector2f(10f, 10f))

    val useMaxLight by boolean("use_max_light", true)

    val maxWidth by float("max_width", 120f, 9f, 480f)

    val textureColor by color("texture_color", Color.ofRGB(0xFF8899))

    val textColor by color("text_color", Color.ofRGB(0xFFFFFF))

    val duration by duration("duration", 10.seconds, Duration.ZERO, 60.seconds)

    val fadeInDuration by duration("fade_in_duration", 0.25.seconds, 0.seconds, 2.seconds)

    val fadeOutDuration by duration("fade_out_duration", 0.25.seconds, 0.seconds, 2.seconds)

    val serverChatBubbleConfig by stringChatBubbleServerConfigMap(
        "server_chat_bubble_config", mapOf(
            "#single" to ChatBubbleServerConfig.DEFAULT_CONFIG
        )
    )

    init {
        setGuiWrapper { config, modifier ->
            ChatBubbleConfigGui(config, modifier)
        }
    }

    private val bubbleQueue = ConcurrentLinkedQueue<ChatBubblePair>()

    @JvmStatic
    fun addChatBubble(text: McText, uuid: UUID?, profile: GameProfile?) {
        if (!enabled) return
        ChatBubble.fromChatMessage(text.string, uuid, profile)?.let { bubbleQueue.add(it) }
    }

    fun render(
        player: AbstractClientPlayer,
        packedLight: Int,
        renderState: AvatarRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector
    ) {
        bubbleQueue.filter { it.bubble.shouldRemove }
            .let { bubbleQueue.removeAll(it) }

        bubbleQueue.findLast {
            it.matcher.match(player)
        }?.bubble?.render(packedLight, renderState, poseStack, nodeCollector)
    }

}

data class ChatBubblePair(
    val matcher: EntityMatcher,
    val bubble: ChatBubble
)

data class ChatBubbleServerConfig(
    val regex: String = "<(?<name>[^>]+)>\\s(?<message>.+)",
    val enableUUID: Boolean = false,
    val enableProfile: Boolean = false,
) : Serializable {
    companion object : Deserializer<ChatBubbleServerConfig> {
        val DEFAULT_CONFIG = ChatBubbleServerConfig()
        override fun deserialization(serializeElement: SerializeElement): ChatBubbleServerConfig {
            return serializeElement.checkType<SerializeObject, ChatBubbleServerConfig> {
                ChatBubbleServerConfig(
                    it["regex"]!!.asString,
                    it["enable_uuid"]!!.asBoolean,
                    it["enable_profile"]!!.asBoolean
                )
            }.getOrThrow()
        }
    }

    override fun serialization(): SerializeElement = serializeObject {
        "regex" to regex
        "enable_uuid" to enableUUID
        "enable_profile" to enableProfile
    }

}