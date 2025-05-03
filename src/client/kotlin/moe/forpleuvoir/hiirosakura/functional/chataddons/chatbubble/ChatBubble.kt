package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker
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
import java.util.regex.Pattern
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.TimeSource.Monotonic.ValueTimeMark

class ChatBubble(
    val timeMark: ValueTimeMark = TimeSource.Monotonic.markNow(),
    val playerName: String,
    val message: String
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

        private const val DEFAULT_REGEX = "<(?<name>[^>]+)>\\s(?<message>.+)"

        fun fromChatMessage(message: String): ChatBubble? {
            val msg = message.replace("(§.)", "")

            ChatBubbleHandler.matchMapping.forEach { (serverName, regex) ->
                if (mc.server != null && serverName == "") {
                    return extractPlayerMessage(regex, msg)
                } else if (currentServerName == serverName) {
                    return extractPlayerMessage(regex, msg)
                }
            }

            return extractPlayerMessage(DEFAULT_REGEX, msg)
        }

        private fun extractPlayerMessage(regex: String, message: String): ChatBubble? {
            runCatching {
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(message)
                if (matcher.find()) {
                    val name: String? = matcher.group(NAME_GROUP)
                    val msg: String? = matcher.group(MESSAGE_GROUP)
                    if (name != null && msg != null) {
                        return ChatBubble(
                            playerName = name,
                            message = msg
                        )
                    }
                }
            }
            return null
        }

        private fun calculateAlpha(
            duration: Duration,
            fadeInDuration: Duration,
            fadeOutDuration: Duration,
            timeMark: ValueTimeMark
        ): Float {
            val progress = (timeMark.elapsedNow() / duration).coerceIn(0.0, 1.0)
            val fadeInRatio = (fadeInDuration / duration).coerceIn(0.001, 1.0)
            val fadeOutRatio = (fadeOutDuration / duration).coerceIn(0.001, 1.0)

            // 计算透明度
            val alpha = when {
                progress < fadeInRatio      -> (progress / fadeInRatio).toFloat()
                progress < 1 - fadeOutRatio -> 1f
                else                        -> (1f - (progress - (1 - fadeOutRatio)) / fadeOutRatio).toFloat()
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

    val shouldRemove: Boolean get() = timeMark.elapsedNow() > ChatBubbleHandler.duration

    fun render(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider.Immediate, light: Int) {
        val alpha = calculateAlpha(
            ChatBubbleHandler.duration,
            ChatBubbleHandler.fadeInDuration,
            ChatBubbleHandler.fadeOutDuration,
            timeMark
        ).coerceIn(0.05f, 1f)
        matrices.push()

        val offset = ChatBubbleHandler.offset
        matrices.translate(offset.x(), offset.y() + 1.15f, 0f)

        val camera = mc.gameRenderer.camera

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-camera.yaw * (Math.PI.toFloat() / 180F)))
        if (!ChatBubbleHandler.onlyYRotation) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(camera.pitch * (Math.PI.toFloat() / 180F))) // 垂直方向
        }

        val scale = ChatBubbleHandler.scale
        val s = -0.025f

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