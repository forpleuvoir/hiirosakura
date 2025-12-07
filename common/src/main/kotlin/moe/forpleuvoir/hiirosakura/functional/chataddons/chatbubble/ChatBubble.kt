package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.vertex.PoseStack
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.EntityMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.EntityMatcher
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon.useIrisCompatiblePipeline
import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.hiirosakura.render.HSRenderType
import moe.forpleuvoir.hiirosakura.render.pushStringLines
import moe.forpleuvoir.hiirosakura.render.pushTexture
import moe.forpleuvoir.hiirosakura.util.resourceLocation
import moe.forpleuvoir.ibukigourd.gui.base.extensions.guigraphics.useMatrixStack
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.IGGuiGraphics
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.ibukigourd.text.size
import moe.forpleuvoir.ibukigourd.text.wrapToLines
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import org.joml.Quaternionf
import org.joml.Vector2f
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

        internal val TEXTURE = TextureInfo(16, 16, resourceLocation("texture/gui/chat/bubble.png"))

        private val BUBBLE = WidgetTexture(Corner(4), 0, 0, 16, 10, TEXTURE)

        private val ARROW = WidgetTexture(Corner(top = -1), 5, 11, 12, 14, TEXTURE)

        private val RENDER_TYPE get() = if (useIrisCompatiblePipeline.value) IRIS_RENDER_TYPE else VANILLA_RENDER_TYPE

        private val VANILLA_RENDER_TYPE = HSRenderType.POSITION_TEX_COLOR.apply(TEXTURE.texture)

        private val IRIS_RENDER_TYPE = RenderType.entityTranslucent(TEXTURE.texture)

        private const val LINE_SPACING = 4f

        private val currentServerName: String get() = ServerMarker.lastServerName

        private const val NAME_GROUP = "name"

        private const val MESSAGE_GROUP = "message"

        fun fromChatMessage(message: String, uuid: UUID?, profile: GameProfile?): ChatBubblePair? {
            val message = message.replace("(§.)", "")
            //有配置
            val config = ChatBubbleHandler.serverChatBubbleConfig.asSequence().firstOrNull { (serverName, _) ->
                //单人游戏                                          多人游戏
                (mc.services() != null && serverName == "#single") || serverName == currentServerName
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
            textureBox: Box,
            arrowBox: Box,
            textBox: Box,
            lines: List<String>,
            alpha: Float,
            poseStack: PoseStack,
            offset: Vector2fc,
            scale: Vector2fc,
            nodeCollector: SubmitNodeCollector,
            packedLight: Int
        ) {
            val packedLight = ChatBubbleHandler.useMaxLight.pick(LightTexture.FULL_BRIGHT, packedLight)

            poseStack.pushPose()

            val s = -0.025f
            val scaledOffset = Vector2f()
            offset.mul(0.25f, scaledOffset)

            poseStack.translate(scaledOffset.x(), scaledOffset.y() + 1.05f + textBox.halfHeight * -s, 0f)

            val camera = mc.gameRenderer.mainCamera
            val cameraYaw = camera.yRot
            val cameraPitch = camera.xRot
            poseStack.mulPose(Quaternionf().rotateY(-cameraYaw * (Math.PI.toFloat() / 180F)))// 水平旋转
            if (!ChatBubbleHandler.onlyYRotation)
                poseStack.mulPose(Quaternionf().rotateX(cameraPitch * (Math.PI.toFloat() / 180F))) // 垂直旋转

            poseStack.scale(scale.x() * s, scale.y() * s, -s)
            nodeCollector.pushTexture(arrowBox, ARROW, packedLight, ChatBubbleHandler.textureColor.alpha(alpha), poseStack, RENDER_TYPE)
            //有点无语,Iris疑似不让用自定义的渲染管线.导致用不了多边形偏移,只能调整气泡的位置了
            poseStack.translate(0f, 0f, 0.015f)
            nodeCollector.pushTexture(textureBox, BUBBLE, packedLight, ChatBubbleHandler.textureColor.alpha(alpha), poseStack, RENDER_TYPE)
            if (PLATFORM.getPlatformName() == "Neoforge") {
                poseStack.translate(0f, 0f, 0f)
            }
            nodeCollector.pushStringLines(
                lines,
                textBox,
                Alignment.Left,
                Arrangement.spacedBy(LINE_SPACING),
                displayMode = Font.DisplayMode.POLYGON_OFFSET,
                dropShadow = false,
                defaultColor = ChatBubbleHandler.textColor.alpha(alpha),
                packedLight = packedLight,
                poseStack = poseStack
            )

            poseStack.popPose()
        }

        private fun renderBubbleInGui(
            box: Box,
            textureBox: Box,
            arrowBox: Box,
            textBox: Box,
            lines: List<String>,
            alpha: Float,
            offset: Vector2fc,
            scale: Vector2fc,
            guiGraphics: IGGuiGraphics,
        ) {
            guiGraphics.useMatrixStack {
                it.translation(box.center.x(), box.top - textBox.halfHeight)
                val offsetMul = 5f
                it.translate(offset.x() * offsetMul, -offset.y() * offsetMul)
                it.scale(scale)
                pushWidgetTexture(textureBox, BUBBLE, ChatBubbleHandler.textureColor.alpha(alpha))
                pushWidgetTexture(arrowBox, ARROW, ChatBubbleHandler.textureColor.alpha(alpha))
                pushStringLines(lines, textBox, Alignment.Left, Arrangement.spacedBy(LINE_SPACING), defaultColor = ChatBubbleHandler.textColor.alpha(alpha))
            }

        }

    }

    private val lines: List<String> = message.wrapToLines(ChatBubbleHandler.maxWidth)

    private val textBox: Box

    private val textureBox: Box

    private val arrowBox: Box

    init {
        val (maxWidth, height) = lines.size(LINE_SPACING)
        textBox = Box(x = -maxWidth / 2, y = -height / 2, maxWidth, height)
        textureBox = textBox.expandEdges(5f, 4f, 4f, 4f)
        arrowBox = Box(textBox.center.x() - ARROW.width / 2, textureBox.bottom, Size(ARROW.width, ARROW.height - ARROW.corner.top))
    }

    val shouldRemove: Boolean get() = timeMark.elapsedNow() > duration

    fun render(
        yRot: Float,
        xRot: Float,
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
        renderBubble(textureBox, arrowBox, textBox, lines, alpha, poseStack, offset, scale, nodeCollector, packedLight)
    }

    fun renderInGui(guiGraphics: IGGuiGraphics, box: Box) {
        val alpha = calculateAlpha(
            duration,
            fadeInDuration,
            fadeOutDuration,
            timeMark
        ).coerceIn(0.05f, 1f)
        renderBubbleInGui(box, textureBox, arrowBox, textBox, lines, alpha, ChatBubbleHandler.offset, ChatBubbleHandler.scale, guiGraphics)
    }

}

