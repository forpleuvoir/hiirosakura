package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import com.mojang.authlib.GameProfile
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.EntityMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.EntityMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderText
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderTextureColored
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.positionMatrix
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.ibukigourd.render.*
import moe.forpleuvoir.ibukigourd.text.size
import moe.forpleuvoir.ibukigourd.text.wrapToLines
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import org.joml.Vector2fc
import java.util.*
import java.util.regex.Pattern
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.TimeSource.Monotonic.ValueTimeMark

class ChatBubble(
    val message: String,
    val timeMark: ValueTimeMark = TimeSource.Monotonic.markNow(),
    val duration: Duration = ChatBubbleHandler.duration,
    val fadeInDuration: Duration = ChatBubbleHandler.fadeInDuration,
    val fadeOutDuration: Duration = ChatBubbleHandler.fadeOutDuration,
) {
    companion object {

        private val textRenderer get() = mc.textRenderer

        private val TEXTURE = TextureInfo(16, 16, identifier("texture/gui/chat/bubble.png"))

        private val BUBBLE = WidgetTexture(Corner(4), 0, 0, 16, 10, TEXTURE)

        private val ARROW = WidgetTexture(Corner(top = -1), 5, 11, 12, 14, TEXTURE)

        private const val LINE_SPACING = 4f

        private val currentServerName: String? get() = ServerMarker.lastServerName

        private const val NAME_GROUP = "name"
        private const val MESSAGE_GROUP = "message"

        fun fromChatMessage(message: String, uuid: UUID?, profile: GameProfile?): ChatBubblePair? {
            val message = message.replace("(§.)", "")
            //有配置
            val config = ChatBubbleHandler.serverChatBubbleConfig.asSequence().firstOrNull { (serverName, _) ->
                //单人游戏                                          多人游戏
                (mc.server != null && serverName == "#single") || serverName == currentServerName
            }?.value ?: ChatBubbleServerConfig.DEFAULT_CONFIG //没有配置 使用默认配置

            val (chatBubble, name) = extractPlayerMessage(config.regex, message) ?: return null
            return ChatBubblePair(
                EntityMatcher(
                    MultiMatcher.MatchMode.AnyMatch,
                    buildList {
                        add(EntityMatchEntry.Name(name))
                        add(EntityMatchEntry.DisplayName(name))
                        if (config.enableUUID && uuid != null) {
                            add(EntityMatchEntry.UUID(uuid))
                        }
                        if (config.enableProfile && profile != null) {
                            add(EntityMatchEntry.Name(profile.name))
                            add(EntityMatchEntry.DisplayName(profile.name))
                        }
                    }
                ),
                chatBubble
            )
        }

        private fun extractPlayerMessage(regex: String, message: String): Pair<ChatBubble, String>? {
            runCatching {
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(message)
                if (matcher.find()) {
                    val name: String? = matcher.group(NAME_GROUP)
                    val msg: String? = matcher.group(MESSAGE_GROUP)
                    if (name != null && msg != null) {
                        return ChatBubble(msg) to name
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
            return null
        }

        private fun calculateAlpha(
            duration: Duration,
            fadeInDuration: Duration,
            fadeOutDuration: Duration,
            timeMark: ValueTimeMark
        ): Float {
            val elapsedTime = timeMark.elapsedNow()

            // 计算透明度
            val alpha = when {
                elapsedTime < fadeInDuration ->
                    (elapsedTime / fadeInDuration).toFloat().coerceIn(0.0f, 1.0f)

                elapsedTime > duration - fadeOutDuration ->
                    (1f - ((elapsedTime - (duration - fadeOutDuration)) / fadeOutDuration)).toFloat().coerceIn(0.0f, 1.0f)

                else -> 1f
            }
            return alpha
        }

    }

    private val lines: List<String> = message.wrapToLines(ChatBubbleHandler.maxWidth)

    private val textBox: Box

    private val textureBox: Box

    private val arrowBox: Box

    init {
        val (maxWidth, height) = lines.size(LINE_SPACING)
        textBox = Box(x = -maxWidth / 2, y = -height / 2, maxWidth, height)
        textureBox = textBox.expandEdges(4f)
        arrowBox = Box(textBox.center.x() - ARROW.width / 2, textureBox.bottom, Size(ARROW.width, ARROW.height - ARROW.corner.top))
    }

    val shouldRemove: Boolean get() = timeMark.elapsedNow() > duration

    fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider.Immediate,
        facingCamera: Boolean = true,
        scale: Vector2fc = ChatBubbleHandler.scale,
        offset: Vector2fc = ChatBubbleHandler.offset
    ) {
        val alpha = calculateAlpha(
            duration,
            fadeInDuration,
            fadeOutDuration,
            timeMark
        ).coerceIn(0.05f, 1f)
        matrices.push()

        val s = -0.025f

        matrices.translate(offset.x(), offset.y() + 1.05f + textBox.halfHeight * -s, 0f)

        if (facingCamera) {
            val camera = mc.gameRenderer.camera
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-camera.yaw * (Math.PI.toFloat() / 180F)))
            if (!ChatBubbleHandler.onlyYRotation) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotation(camera.pitch * (Math.PI.toFloat() / 180F))) // 垂直方向
            }
        }

        matrices.scale(scale.x() * s, scale.y() * s, -s)
        enableDepthTest()

        enablePolygonOffset()
        polygonOffset(0f, 5f)
        batchRenderTextureColored(matrices) {
            pushWidgetTexture(textureBox, BUBBLE, ChatBubbleHandler.textureColor.alpha(alpha))
        }
        disablePolygonOffset()

        enablePolygonOffset()
        polygonOffset(0, 0f)
        batchRenderTextureColored(matrices) {
            pushWidgetTexture(arrowBox, ARROW, ChatBubbleHandler.textureColor.alpha(alpha))
        }
        disablePolygonOffset()

        polygonOffset(0, 10f)
        textRenderer.batchRenderText(vertexConsumers, matrices.positionMatrix) {
            pushStringLines(lines, textBox, Alignment.Left, Arrangement.spacedBy(LINE_SPACING), defaultColor = ChatBubbleHandler.textColor.alpha(alpha))
        }
        polygonOffset(0, 0)
        disablePolygonOffset()
        disableDepthTest()
        matrices.pop()
    }

}