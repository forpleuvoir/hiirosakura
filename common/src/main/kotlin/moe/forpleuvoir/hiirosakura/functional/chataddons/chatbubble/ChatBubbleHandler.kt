package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.config.items.configChatBubbleServerConfigMap
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.EntityMatcher
import moe.forpleuvoir.ibukigourd.config.item.configVector2f
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.ui.configwrapper.uiWrapper
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configBoolean
import moe.forpleuvoir.nebula.config.item.configColor
import moe.forpleuvoir.nebula.config.item.configDuration
import moe.forpleuvoir.nebula.config.item.configFloat
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import org.joml.Vector2f
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object ChatBubbleHandler : ConfigGroup("chat_bubble") {

    val enabled by configBoolean("enable", false)

    val onlyYRotation by configBoolean("only_y_rotation", true)

    val offset by configVector2f("offset", Vector2f(0f, 0.0f), Vector2f(-10f, -1f), Vector2f(10f, 10f))

    val scale by configVector2f("scale", Vector2f(1f, 1f), Vector2f(0.1f, 0.1f), Vector2f(10f, 10f))

    val useMaxLight by configBoolean("use_max_light", false)

    val maxWidth by configFloat("max_width", 120f, 9f, 480f)

    val textureColor by configColor("texture_color", Color.fromRGB(0xFF8899))

    val textColor by configColor("text_color", Color.fromRGB(0xFFFFFF))

    val duration by configDuration("duration", 10.seconds, Duration.ZERO, 60.seconds)

    val fadeInDuration by configDuration("fade_in_duration", 0.25.seconds, 0.seconds, 2.seconds)

    val fadeOutDuration by configDuration("fade_out_duration", 0.25.seconds, 0.seconds, 2.seconds)

    val serverChatBubbleConfig by configChatBubbleServerConfigMap(
        "server_chat_bubble_config", mapOf(
            "#single" to ChatBubbleServerConfig.DEFAULT_CONFIG
        )
    )

    init {
//        uiWrapper { config ->
//            ChatBubbleConfigGui(config)
//        }
    }

    private val bubbleQueue = ConcurrentLinkedQueue<ChatBubblePair>()

    @JvmStatic
    fun addChatBubble(text: Text, uuid: UUID?, profile: GameProfile?) {
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
            .let { bubbleQueue.removeAll(it.toSet()) }

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
) {
    companion object : Codec<ChatBubbleServerConfig> by Codec.create<ChatBubbleServerConfig>()
        .field<String>("regex").getter(ChatBubbleServerConfig::regex).codec(Codec.string)
        .field<Boolean>("enable_uuid").getter(ChatBubbleServerConfig::enableUUID).codec(Codec.boolean)
        .field<Boolean>("enable_profile").getter(ChatBubbleServerConfig::enableProfile).codec(Codec.boolean)
        .build(::ChatBubbleServerConfig) {

        val DEFAULT_CONFIG = ChatBubbleServerConfig()

    }

}