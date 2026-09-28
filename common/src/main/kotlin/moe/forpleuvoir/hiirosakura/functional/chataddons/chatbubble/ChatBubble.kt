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
import net.minecraft.network.chat.Style
import moe.forpleuvoir.ibukigourd.render.extension.texture.IGTexture
import moe.forpleuvoir.ibukigourd.ui.sokitsu.texture.TextureFill
import moe.forpleuvoir.ibukigourd.ui.sokitsu.texture.atlas.SokitsuAtlasManager
import moe.forpleuvoir.ibukigourd.ui.sokitsu.texture.atlas.SokitsuSprite
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

        /** 气泡体 / 箭头所在的 sokitsu 图集与纹理 id（源文件在 `texture/sokitsu/ui/chat/`）。 */
        private val ATLAS_ID = identifier("ui")

        private val BUBBLE_TEXTURE_ID = identifier("ui/chat/bubble")

        private val ARROW_TEXTURE_ID = identifier("ui/chat/arrow")

        /** 气泡体精灵：九宫格边框等取自纹理定义。 */
        internal val BUBBLE: SokitsuSprite get() = SokitsuAtlasManager.sprite(ATLAS_ID, BUBBLE_TEXTURE_ID)

        /** 箭头精灵。 */
        internal val ARROW: SokitsuSprite get() = SokitsuAtlasManager.sprite(ATLAS_ID, ARROW_TEXTURE_ID)

        /** 箭头布局盒高度（像素）：贴图高度扣掉负 border 的外侧像素。 */
        internal val ARROW_BOX_HEIGHT: Int get() = ARROW.layerHeight() - ARROW.outsetTop() - ARROW.outsetBottom()

        /** 气泡体九宫格的四条内边距（像素）：内容要按它们内缩。 */
        internal val BUBBLE_PADDING_LEFT: Int get() = BUBBLE.paddingLeft().toInt()

        internal val BUBBLE_PADDING_TOP: Int get() = BUBBLE.paddingTop().toInt()

        internal val BUBBLE_PADDING_RIGHT: Int get() = BUBBLE.paddingRight().toInt()

        internal val BUBBLE_PADDING_BOTTOM: Int get() = BUBBLE.paddingBottom().toInt()

        private val RENDER_TYPE get() = if (useIrisCompatiblePipeline.enabled) IRIS_RENDER_TYPE else VANILLA_RENDER_TYPE

        private val VANILLA_RENDER_TYPE = HSRenderType.POSITION_TEX_COLOR.apply(ATLAS_ID)

        private val IRIS_RENDER_TYPE = RenderTypes.entityTranslucent(ATLAS_ID)

        /** 九宫格边框的左内边距（像素）；纹理未加载时用缺省值。 */
        internal fun SokitsuSprite.paddingLeft(): Float =
            ((layers.firstOrNull()?.fill as? TextureFill.NinePatch)?.border?.left ?: 5).toFloat()

        /** 九宫格边框的上内边距（像素）。 */
        internal fun SokitsuSprite.paddingTop(): Float =
            ((layers.firstOrNull()?.fill as? TextureFill.NinePatch)?.border?.top ?: 4).toFloat()

        /** 九宫格边框的右内边距（像素）。 */
        internal fun SokitsuSprite.paddingRight(): Float =
            ((layers.firstOrNull()?.fill as? TextureFill.NinePatch)?.border?.right ?: 5).toFloat()

        /** 九宫格边框的下内边距（像素）。 */
        internal fun SokitsuSprite.paddingBottom(): Float =
            ((layers.firstOrNull()?.fill as? TextureFill.NinePatch)?.border?.bottom ?: 5).toFloat()

        /** 图层宽度（像素）；纹理未加载时为 0。 */
        internal fun SokitsuSprite.layerWidth(): Int = layers.firstOrNull()?.width ?: 0

        /** 图层高度（像素）；纹理未加载时为 0。 */
        internal fun SokitsuSprite.layerHeight(): Int = layers.firstOrNull()?.height ?: 0

        /** 负 border 在左/右/上/下的外侧像素数（布局盒不含这些）。 */
        internal fun SokitsuSprite.outsetLeft(): Int = (-paddingLeft().toInt()).coerceAtLeast(0)

        internal fun SokitsuSprite.outsetRight(): Int = (-paddingRight().toInt()).coerceAtLeast(0)

        internal fun SokitsuSprite.outsetTop(): Int = (-paddingTop().toInt()).coerceAtLeast(0)

        internal fun SokitsuSprite.outsetBottom(): Int = (-paddingBottom().toInt()).coerceAtLeast(0)

        /** 把精灵转成世界 blit 用的 [IGTexture]（图集尺寸 + 图层像素 UV + 图层九宫格角）。 */
        private fun SokitsuSprite.toIGTexture(): IGTexture? {
            val atlas = SokitsuAtlasManager.atlasTexture(atlasLocation) ?: return null
            val layer = layers.firstOrNull() ?: return null
            return IGTexture(
                Corner(paddingLeft().toInt(), paddingRight().toInt(), paddingTop().toInt(), paddingBottom().toInt()),
                layer.x,
                layer.y,
                layer.x + layer.width,
                layer.y + layer.height,
                TextureInfo(atlas.width, atlas.height, atlas.location),
            )
        }

        internal const val LINE_SPACING = 4f

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
            packedLight: Int,
            faceCamera: Boolean
        ) {
            val packedLight = (ChatBubbleHandler.useMaxLight || !useIrisCompatiblePipeline.enabled).either(LightCoordsUtil.FULL_BRIGHT, packedLight)

            poseStack.pushPose()
            val s = -0.025f * renderState.scale
            val height = renderState.boundingBoxHeight * 0.5f
            val scaledOffset = offset * 0.25f

            poseStack.translate(0f, height + arrowArea.bottom * -s, 0f)
            poseStack.translate(scaledOffset.x(), scaledOffset.y(), 0f)

            if (faceCamera) {
                val camera = mc.gameRenderer.mainCamera()
                poseStack.mulPose(Quaternionf().rotateY(-camera.yRot() * (Math.PI.toFloat() / 180F)))// 水平旋转
                if (!ChatBubbleHandler.onlyYRotation)
                    poseStack.mulPose(Quaternionf().rotateX(camera.xRot() * (Math.PI.toFloat() / 180F))) // 垂直旋转
            }

            val bubbleTexture = BUBBLE.toIGTexture()
            val arrowTexture = ARROW.toIGTexture()
            poseStack.scale(scale.x() * s, scale.y() * s, -s)
            if (bubbleTexture != null && arrowTexture != null) {
                nodeCollector.pushSpeechBubbleTexture(
                    bubbleArea,
                    bubbleTexture,
                    arrowArea,
                    arrowTexture,
                    AnchorPosition.Below,
                    packedLight,
                    ChatBubbleHandler.textureColor.alpha(alpha),
                    poseStack,
                    RENDER_TYPE
                )
            }
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

    }

    /** 按 [ChatBubbleHandler.maxWidth] 折行后的消息文本。 */
    internal val lines: List<String> = message.wrapToLines(ChatBubbleHandler.maxWidth)

    /** 文本区：原点在箭头尖端，向上为负 y。 */
    internal val textArea: Rect

    /** 气泡框：文本区四周各留一段内边距。 */
    internal val bubbleArea: Rect

    /** 箭头：贴 [bubbleArea] 底边居中。 */
    internal val arrowArea: Rect

    init {
        val (measuredWidth, measuredHeight) = lines.size(LINE_SPACING)
        val width = measuredWidth
        // 空消息没有行高，兜一行文本的高度（行高取自当前字体，不写死）
        val height = measuredHeight.coerceAtLeast(mc.font.lineHeight.toFloat())
        val maxWidth = width.coerceAtLeast(7f)
        val padLeft = BUBBLE.paddingLeft()
        val padTop = BUBBLE.paddingTop()
        val padRight = BUBBLE.paddingRight()
        val padBottom = BUBBLE.paddingBottom()
        // 布局盒 = 贴图扣掉负 border 的外侧像素（外侧那部分画在锚点线之外）
        val arrowWidth = ARROW.layerWidth() - ARROW.outsetLeft() - ARROW.outsetRight()
        val arrowHeight = ARROW.layerHeight() - ARROW.outsetTop() - ARROW.outsetBottom()
        textArea = Rect(Offset(x = -maxWidth / 2f, y = -height - padBottom - arrowHeight), Size(maxWidth, height))
        bubbleArea = textArea.expandEdges(padLeft, padTop, padRight, padBottom)
        arrowArea = Rect(
            Offset(textArea.center.x - arrowWidth / 2f, bubbleArea.bottom),
            Size(arrowWidth.toFloat(), arrowHeight.toFloat()),
        )
    }

    val shouldRemove: Boolean get() = timeMark.elapsedNow() > duration

    /** 当前透明度：淡入 / 淡出进度，最低 0.05，完全不透明为 1。 */
    val alpha: Float
        get() = calculateAlpha(duration, fadeInDuration, fadeOutDuration, timeMark).coerceIn(0.05f, 1f)

    /**
     * 把气泡画在 [renderState] 对应实体的头顶。
     *
     * @param faceCamera 是否让气泡绕 Y 轴（以及非仅 Y 轴模式下的 X 轴）转向游戏相机；
     *   配置页预览的取景与游戏相机无关，那里传 false 保持模型的自身朝向。
     */
    fun render(
        packedLight: Int,
        renderState: AvatarRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector,
        scale: Vector2fc = ChatBubbleHandler.scale,
        offset: Vector2fc = ChatBubbleHandler.offset,
        faceCamera: Boolean = true
    ) {
        renderBubble(bubbleArea, arrowArea, textArea, lines, alpha, poseStack, offset, scale, renderState, nodeCollector, packedLight, faceCamera)
    }

}
