package forpleuvoir.hiirosakura.client.feature.chatbubble

import com.mojang.blaze3d.systems.RenderSystem
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.HiiroSakuraClient.mc
import forpleuvoir.hiirosakura.client.HiiroSakuraClient.tickCounter
import forpleuvoir.hiirosakura.client.config.Configs.Values
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_BUBBLE_ONLY_Y_ROTATION
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_BUBBLE_TEXTURE_COLOR
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_BUBBLE_VANILLA_SHADER
import forpleuvoir.hiirosakura.client.feature.regex.ChatMessageRegex
import forpleuvoir.ibuki_gourd.utils.color.Color4f
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode.QUADS
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3f
import java.util.*

/**
 * 聊天消息泡泡
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.chatbubble
 *
 * 文件名 ChatBubble
 *
 * 创建时间 2021/6/12 21:47
 */
class ChatBubble(private val text: String, private val playerName: String) {


    /**
     * 显示时间
     */
    private val timer: Long = tickCounter + Values.CHAT_BUBBLE_TIME.getValue()
    private val lines: List<Text>
    private val textRenderer: TextRenderer = MinecraftClient.getInstance().textRenderer
    var shouldRemove: Boolean = false

    /**
     * 渲染文本
     *
     * @param player      玩家
     * @param dispatcher  [EntityRenderDispatcher]
     * @param matrixStack [MatrixStack]
     */
    fun render(player: AbstractClientPlayerEntity, dispatcher: EntityRenderDispatcher, matrixStack: MatrixStack) {
        if (timer <= tickCounter) {
            shouldRemove = true
            return
        }
        if (player.entityName != playerName) return
        val width = maxWidth
        val count = lines.size
        val lineSpacing = 4
        val height = getHeight(count)
        matrixStack.push()
        matrixStack.translate(0.0, player.height + Values.CHAT_BUBBLE_HEIGHT.getValue(), 0.0)
        val scale = -0.025f * Values.CHAT_BUBBLE_SCALE.getValue().toFloat()
        matrixStack.scale(scale, scale, scale)
        if (CHAT_BUBBLE_ONLY_Y_ROTATION.getValue()) {
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(dispatcher.rotation.toEulerXyzDegrees().y))
        } else {
            matrixStack.multiply(dispatcher.rotation)
        }
        RenderSystem.enableDepthTest()
        RenderSystem.enablePolygonOffset()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShaderTexture(0, BUBBLE_TEXTURE)
        renderBackground(matrixStack, width + 5, height + lineSpacing)
        for ((index, text) in lines.withIndex()) {
            textRenderer.draw(
                matrixStack,
                text,
                -(width / 2).toFloat(),
                (-height + getHeight(count, index = index)).toFloat(),
                Values.CHAT_BUBBLE_TEXT_COLOR.getValue().rgba
            )
        }
        RenderSystem.disableBlend()
        RenderSystem.disablePolygonOffset()
        RenderSystem.disableDepthTest()
        matrixStack.pop()
    }

    private fun renderBackground(matrixStack: MatrixStack, width: Int, height: Int) {
        RenderSystem.polygonOffset(3.0f, 3.0f)
        matrixStack.translate(0.0, 0.0, -Values.CHAT_BUBBLE_BACKGROUND_Z_OFFSET.getValue())
        drawTexture(matrixStack, -(width / 2) - 4, -height - 4, 4, 4, 0f, 0f, 4, 4)
        drawTexture(matrixStack, -(width / 2), -height - 4, width, 4, 4f, 0f, 24, 4)
        drawTexture(matrixStack, width / 2, -height - 4, 4, 4, 28f, 0f, 4, 4)
        drawTexture(matrixStack, -(width / 2) - 4, -height, 4, height, 0f, 4f, 4, 24)
        drawTexture(matrixStack, -(width / 2), -height, width, height, 4f, 4f, 24, 24)
        drawTexture(matrixStack, width / 2, -height, 4, height, 28f, 4f, 4, 24)
        drawTexture(matrixStack, -(width / 2) - 4, 0, 4, 4, 0f, 28f, 4, 4)
        drawTexture(matrixStack, -(width / 2), 0, width, 4, 4f, 28f, 24, 4)
        drawTexture(matrixStack, width / 2, 0, 4, 4, 28f, 28f, 4, 4)
        matrixStack.translate(0.0, 0.0, Values.CHAT_BUBBLE_BACKGROUND_Z_OFFSET.getValue())
        RenderSystem.polygonOffset(0.0F, 0.0F)
    }

    private fun drawTexture(
        matrixStack: MatrixStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        u: Float,
        v: Float,
        regionWidth: Int,
        regionHeight: Int,
    ) {
        val matrix = matrixStack.peek().positionMatrix
        val color = CHAT_BUBBLE_TEXTURE_COLOR.getValue().rgba
        val bufferBuilder = Tessellator.getInstance().buffer
        if (CHAT_BUBBLE_VANILLA_SHADER.getValue()) {
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            val color4f = Color4f().fromInt(color)
            RenderSystem.setShaderColor(color4f.red, color4f.green, color4f.blue, color4f.alpha)
            bufferBuilder.begin(QUADS, VertexFormats.POSITION_TEXTURE)
        } else {
            RenderSystem.setShader { HiiroSakuraChatBubble.shader ?: shader }
            bufferBuilder.begin(QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
        }

        bufferBuilder.vertex(matrix, x.toFloat(), y + height.toFloat(), 0.0f).texture(u / 32f, (v + regionHeight) / 32f)
            .color(color).next()
        bufferBuilder.vertex(matrix, x + width.toFloat(), y + height.toFloat(), 0.0f)
            .texture((u + regionWidth) / 32f, (v + regionHeight) / 32f)
            .color(color)
            .next()
        bufferBuilder.vertex(matrix, x + width.toFloat(), y.toFloat(), 0.0f).texture((u + regionWidth) / 32f, v / 32f)
            .color(color).next()
        bufferBuilder.vertex(matrix, x.toFloat(), y.toFloat(), 0.0f).texture(u / 32f, v / 32f).color(color).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
    }

    /**
     * 处理文本
     * 多字符换行并去掉玩家名
     *
     * @return 处理之后的文本 [List]
     */
    private fun handleText(maxWidth: Int): List<Text> {
        val list: MutableList<Text> = LinkedList()
        val sb = StringBuilder()
        for (c in text.toCharArray()) {
            if (textRenderer.getWidth(sb.toString()) > maxWidth) {
                list.add(LiteralText(sb.toString()))
                sb.clear()
            }
            sb.append(c)
        }
        list.add(LiteralText(sb.toString()))
        return list
    }

    private val maxWidth: Int
        get() {
            var max = 0
            for (text in lines) {
                if (max < textRenderer.getWidth(text)) {
                    max = textRenderer.getWidth(text)
                }
            }
            return max
        }

    private fun getHeight(count: Int, textHeight: Int = 9, lineSpacing: Int = 4): Int {
        var height = textHeight
        for (i in 0 until count) {
            if (i != 0) {
                height += textHeight + lineSpacing
            }
        }
        return height
    }

    private fun getHeight(count: Int, textHeight: Int = 9, lineSpacing: Int = 4, index: Int): Int {
        var height = -lineSpacing / 2
        for (i in 0 until count) {
            if (i != 0) {
                height += textHeight + lineSpacing
            }
            if (i == index) {
                return height
            }
        }
        return height
    }

    companion object {
        private val BUBBLE_TEXTURE = Identifier(
            HiiroSakuraClient.modId,
            "texture/gui/feature/chatshow/bubble.png"
        )
        private val shader: Shader =
            Shader(mc.resourcePackProvider.pack, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR)

        @JvmStatic
        fun getInstance(chatMessageRegex: ChatMessageRegex): ChatBubble? {
            return try {
                chatMessageRegex.message?.let { message ->
                    chatMessageRegex.playerName?.let { playerName ->
                        ChatBubble(
                            message,
                            playerName
                        )
                    }
                }
            } catch (ignored: Exception) {
                null
            }
        }
    }

    init {
        lines = handleText(Values.CHAT_BUBBLE_TEXT_MAX_WIDTH.getValue())
    }
}