package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.EntityMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.EntityMatcher
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon.useIrisCompatiblePipeline
import moe.forpleuvoir.hiirosakura.render.HSRenderType
import moe.forpleuvoir.hiirosakura.render.pushSpeechBubbleTexture
import moe.forpleuvoir.hiirosakura.render.pushStringLines
import moe.forpleuvoir.hiirosakura.util.expandEdges
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.ibukigourd.render.extension.AnchorPosition
import moe.forpleuvoir.ibukigourd.render.extension.pushSpeechBubbleTexture
import moe.forpleuvoir.ibukigourd.render.extension.pushStringLines
import moe.forpleuvoir.ibukigourd.render.extension.texture.Corner
import moe.forpleuvoir.ibukigourd.render.extension.texture.IGTexture
import moe.forpleuvoir.ibukigourd.render.extension.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.text.size
import moe.forpleuvoir.ibukigourd.text.wrapToLines
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.util.primitive.either
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.util.LightCoordsUtil
import org.joml.Quaternionf
import org.joml.Vector2fc
import org.joml.times
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

        internal val TEXTURE = TextureInfo(16, 16, identifier("texture/gui/chat/bubble.png"))

        private val BUBBLE = IGTexture(Corner(4), 0, 0, 16, 9, TEXTURE)

        private val ARROW = IGTexture(Corner(top = -4), 5, 13, 12, 16, TEXTURE)

        private val RENDER_TYPE get() = if (useIrisCompatiblePipeline.enabled) IRIS_RENDER_TYPE else VANILLA_RENDER_TYPE

        private val VANILLA_RENDER_TYPE = HSRenderType.POSITION_TEX_COLOR.apply(TEXTURE.textureId)

        private val IRIS_RENDER_TYPE = RenderTypes.entityTranslucent(TEXTURE.textureId)

        private const val LINE_SPACING = 4f

        private val currentServerName: String get() = ServerMarker.lastServerName

        private const val NAME_GROUP = "name"

        private const val MESSAGE_GROUP = "message"

        fun fromChatMessage(message: String, uuid: UUID?, profile: GameProfile?): ChatBubblePair? {
            val message = message.replace("(§.)".toRegex(), "")
            //有配置
            val config = ChatBubbleHandler.serverChatBubbleConfig.asSequence().firstOrNull { (serverName, _) ->
                //单人游戏                   多人游戏
                serverName == "#single" || serverName == currentServerName
            }?.value ?: ChatBubbleServerConfig.DEFAULT_CONFIG //没有配置 使用默认配置

            val (chatBubble, name) = extractPlayerMessage(config.regex, message) ?: return null
            return ChatBubblePair(
                EntityMatcher(
                    CompositeMatcher.MatchMode.AnyMatch,
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

        private fun renderBubble(
            bubbleArea: Rect,
            arrowArea: Rect,
            textArea: Rect,
            lines: List<String>,
            alpha: Float,
            poseStack: PoseStack,
            offset: Vector2fc,
            scale: Vector2fc,
            renderState: AvatarRenderState,
            nodeCollector: SubmitNodeCollector,
            packedLight: Int
        ) {
            val packedLight = (ChatBubbleHandler.useMaxLight || !useIrisCompatiblePipeline.enabled).either(LightCoordsUtil.FULL_BRIGHT, packedLight)

            poseStack.pushPose()
            val s = -0.025f * renderState.scale
            val height = renderState.boundingBoxHeight * 0.5f
            val scaledOffset = offset * 0.25f

            poseStack.translate(0f, height + arrowArea.bottom * -s, 0f)
            poseStack.translate(scaledOffset.x(), scaledOffset.y(), 0f)

            val camera = mc.gameRenderer.mainCamera
            val cameraYaw = camera.yRot()
            val cameraPitch = camera.xRot()
            poseStack.mulPose(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!ChatBubbleHandler.onlyYRotation)
                poseStack.mulPose(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转

            poseStack.scale(scale.x() * s, scale.y() * s, -s)
            nodeCollector.pushSpeechBubbleTexture(
                bubbleArea,
                BUBBLE,
                arrowArea,
                ARROW,
                AnchorPosition.Below,
                packedLight,
                ChatBubbleHandler.textureColor.alpha(alpha),
                poseStack,
                RENDER_TYPE
            )
            nodeCollector.pushStringLines(
                lines,
                textArea.roundToIntRect(),
                Alignment.Start,
                Arrangement.spacedBy(LINE_SPACING.dp),
                displayMode = Font.DisplayMode.POLYGON_OFFSET,
                dropShadow = false,
                defaultColor = ChatBubbleHandler.textColor.alpha(alpha),
                packedLight = packedLight,
                poseStack = poseStack
            )

            poseStack.popPose()
        }

        private fun renderBubbleInGui(
            area: Rect,
            bubbleArea: Rect,
            arrowArea: Rect,
            textArea: Rect,
            lines: List<String>,
            alpha: Float,
            offset: Vector2fc,
            scale: Vector2fc,
            guiGraphics: GuiGraphicsExtractor,
        ) {
            guiGraphics.apply {
                pose().popMatrix()
                pose().translation(area.center.x, area.top - textArea.center.y)
                val offsetMul = 5f
                pose().translate(offset.x() * offsetMul, -offset.y() * offsetMul)
                pose().scale(scale)
                pushSpeechBubbleTexture(bubbleArea, BUBBLE, arrowArea, ARROW, AnchorPosition.Above, ChatBubbleHandler.textureColor.alpha(alpha))
                pushStringLines(
                    lines,
                    textArea.roundToIntRect(),
                    Alignment.Start,
                    Arrangement.spacedBy(LINE_SPACING.dp),
                    defaultColor = ChatBubbleHandler.textColor.alpha(alpha)
                )
            }
        }

    }

    private val lines: List<String> = message.wrapToLines(ChatBubbleHandler.maxWidth)

    private val textArea: Rect

    private val bubbleArea: Rect

    private val arrowArea: Rect

    init {
        val (width, height) = lines.size(LINE_SPACING)
        val maxWidth = width.coerceAtLeast(7f)
        textArea = Rect(Offset(x = -maxWidth / 2f, y = -height - 5f - ARROW.height), Size(maxWidth, height))
        bubbleArea = textArea.expandEdges(5f, 4f, 5f, 5f)
        arrowArea = Rect(Offset(textArea.center.x - ARROW.width / 2f, bubbleArea.bottom), Size(ARROW.width.toFloat(), ARROW.height.toFloat()))
    }

    val shouldRemove: Boolean get() = timeMark.elapsedNow() > duration

    fun render(
        packedLight: Int,
        renderState: AvatarRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector,
        scale: Vector2fc = ChatBubbleHandler.scale,
        offset: Vector2fc = ChatBubbleHandler.offset
    ) {
        val alpha = calculateAlpha(
            duration,
            fadeInDuration,
            fadeOutDuration,
            timeMark
        ).coerceIn(0.05f, 1f)
        renderBubble(bubbleArea, arrowArea, textArea, lines, alpha, poseStack, offset, scale, renderState, nodeCollector, packedLight)
    }

    fun renderInGui(guiGraphics: GuiGraphicsExtractor, area: Rect) {
        val alpha = calculateAlpha(
            duration,
            fadeInDuration,
            fadeOutDuration,
            timeMark
        ).coerceIn(0.05f, 1f)
        renderBubbleInGui(area, bubbleArea, arrowArea, textArea, lines, alpha, ChatBubbleHandler.offset, ChatBubbleHandler.scale, guiGraphics)
    }

}

