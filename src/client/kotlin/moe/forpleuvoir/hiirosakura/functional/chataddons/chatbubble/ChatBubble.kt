package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import moe.forpleuvoir.hiirosakura.functional.renderaddons.TntRenderConfig
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderText
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderTextureColored
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.positionMatrix
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.renderBox
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.ibukigourd.render.*
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.wrapToLines
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.regex.Pattern
import kotlin.time.TimeSource
import kotlin.time.TimeSource.Monotonic.ValueTimeMark
import kotlin.times
import kotlin.unaryMinus

class ChatBubble(
    val timeMark: ValueTimeMark = TimeSource.Monotonic.markNow(),
    val playerName: String,
    val message: String
) {
    companion object {

        private val textRenderer get() = mc.textRenderer

        private val TEXTURE = identifier("texture/gui/chat/bubble.png")

        private val BUBBLE = WidgetTexture(Corner(4), 0, 0, 16, 10, TextureInfo(16, 16, TEXTURE))

        private val ARROW = WidgetTexture(Corner(top = -1), 5, 11, 12, 14, TextureInfo(16, 16, TEXTURE))

        private const val LINE_SPACING = 4f

        private val currentServerAddress: String? get() = mc.currentServerEntry?.address

        private const val NAME_GROUP = "name"
        private const val MESSAGE_GROUP = "message"

        private const val DEFAULT_REGEX = "<(?<name>[^>]+)>\\s(?<message>.+)"

        fun fromChatMessage(message: String): ChatBubble? {
            val msg = message.replace("(§.)", "")
            ChatBubbleHandler.Config.matchMapping.forEach { (serverMarkerRegex, regex) ->
                if (mc.server != null && serverMarkerRegex == "#single") {
                    return extractPlayerMessage(regex, msg)
                } else if (currentServerAddress?.matches(Regex(serverMarkerRegex)) == true) {
                    return extractPlayerMessage(regex, msg)
                }
            }
            return extractPlayerMessage(DEFAULT_REGEX, msg)
        }

        private fun extractPlayerMessage(regex: String, message: String): ChatBubble? {
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
            return null
        }

    }

    private val lines: List<String> = message.wrapToLines(textRenderer, ChatBubbleHandler.Config.maxWidth)

    private val textBox: Box

    private val textureBox: Box

    private val arrowBox: Box

    init {
        val maxWidth = lines.maxWidth(textRenderer)
        val height = lines.size * (textRenderer.fontHeight + LINE_SPACING) - LINE_SPACING
        textBox = Box(-maxWidth / 2f, -height, maxWidth, height)
        textureBox = textBox.expandEdges(4f)
        arrowBox = Box(textBox.center.x() - ARROW.width / 2, textureBox.bottom, Size(ARROW.width, ARROW.height - ARROW.corner.top))
    }

    val shouldRemove: Boolean get() = timeMark.elapsedNow() > ChatBubbleHandler.Config.duration

    fun render(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider.Immediate, light: Int) {
        matrices.push()

        val offset = ChatBubbleHandler.Config.offset
        matrices.translate(offset.x(), offset.y() + 1.15f, 0f)

        val camera = mc.gameRenderer.camera
        val cameraYawRad = -camera.yaw * (Math.PI.toFloat() / 180F) // 将摄像机 Yaw 转换为弧度
        val cameraPitchRad = camera.pitch * (Math.PI.toFloat() / 180F) // 将摄像机 Pitch 转换为弧度
        // 根据配置旋转方向
        if (ChatBubbleHandler.Config.onlyYRotation.value) {
            // 仅水平旋转
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(cameraYawRad))
        } else {
            // 同时进行水平 + 垂直旋转
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(cameraYawRad)) // 水平方向
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(cameraPitchRad)) // 垂直方向
        }

        val scale = ChatBubbleHandler.Config.scale
        val s = -0.025f

        matrices.scale(scale.x() * s, scale.y() * s, 1f)
        enableDepthTest()

        enablePolygonOffset()
        polygonOffset(0f, 1f)
        matrices.translate(0f, 0f, -ChatBubbleHandler.Config.backgroundZOffset)
        batchRenderTextureColored(matrices) {
            pushWidgetTexture(textureBox, BUBBLE, ChatBubbleHandler.Config.textureColor)
        }
        disablePolygonOffset()

        enablePolygonOffset()
        polygonOffset(0, 0f)
        batchRenderTextureColored(matrices) {
            pushWidgetTexture(arrowBox, ARROW, ChatBubbleHandler.Config.textureColor)
        }
        matrices.translate(0f, 0f, ChatBubbleHandler.Config.backgroundZOffset)
        disablePolygonOffset()

        polygonOffset(0, 3f)
        textRenderer.batchRenderText(vertexConsumers, matrices.positionMatrix) {
            pushStringLines(lines, textBox, Alignment.Left, Arrangement.spacedBy(LINE_SPACING), defaultColor = ChatBubbleHandler.Config.textColor)
        }
        polygonOffset(0, 0)
        disablePolygonOffset()
        disableDepthTest()
        matrices.pop()
    }

}