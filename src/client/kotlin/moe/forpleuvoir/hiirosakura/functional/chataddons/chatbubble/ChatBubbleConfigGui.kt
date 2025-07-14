package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.ExpandableTextEditor
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderBox
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderTextureColored
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.IGDrawContext
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigsWrapper
import moe.forpleuvoir.ibukigourd.gui.widget.Canvas
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.LockButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.render.defaultZOffset
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.entity.LivingEntity
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.atan
import kotlin.time.Duration.Companion.seconds

fun ContainerScope.ChatBubbleConfigGui(
    config: ChatBubbleHandler,
    modifier: Modifier = Modifier
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            modifier = Modifier.width(80f)
        ) {
            TextLabel(IGLang.setting)
            click {
                Dialog {
                    TextLabel(config.translateText)
                    Row(
                        Modifier.padding(0f)
                    ) {
                        if (mc.player != null) Preview(Modifier.width(145f).height(262f).margin(right = 5f))
                        ConfigsWrapper(
                            config.configs(),
                            modifier = Modifier
                                .maxWidth(400f)
                                .maxHeight(262f)
                                .renderBackground { context, x, y, d ->
                                    context.batchRenderTextureColored {
                                        pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_OUTLINE, Color(0xFFF4D9FF))
                                        pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_INNER, Colors.WHITE)
                                    }
                                }
                                .padding(4),
                            listModifier = {
                                Modifier.weight(1)
                            }
                        )
                    }
                }.open()
            }
        }
        ConfigResetButton(config)
    }
}

private fun ContainerScope.Preview(
    modifier: Modifier = Modifier
) = Column(modifier, verticalArrangement = Arrangement.spacedBy(2f)) {
    val msg = mutableStateOf("Hello Hiirosakura")
    val lock = mutableStateOf(true)
    val chatBubble = mutableStateOf(
        ChatBubble(
            message = msg.getValue(),
            duration = if (lock.getValue()) 114514.seconds else ChatBubbleHandler.duration
        )
    )
    PreviewCanvas(Modifier.width(145f).weight(1), 1f, chatBubble)
    ExpandableTextEditor(msg, HSLang.message, Modifier.matchSibling(), textEditorModifier = { Modifier.weight(1) })
    Row(Modifier.matchSibling()) {
        Button(Modifier.weight(1)) {
            TextLabel(HSLang.send)
            click {
                chatBubble.setValue(
                    ChatBubble(
                        message = msg.getValue(),
                        duration = if (lock.getValue()) 114514.seconds else ChatBubbleHandler.duration
                    )
                )
            }
        }
        LockButton(lock, modifier = Modifier.hoverText(HSLang.chatBubblePreviewLock))
    }
}

private fun ContainerScope.PreviewCanvas(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    chatBubble: State<ChatBubble>
) = Canvas(modifier) { ctx, x, y, d ->
    val box = transform.asWorldCoordinateBox
    ctx.batchRenderBox {
        pushRoundBox(box, Color(244, 217, 255), 4)
        pushRoundBox(box.trimEdges(1f), HSVColor(270f, 0.25f, 0.5f), 4)
    }
    ctx.useMatrixStack {
        it.translate(0f, 0f, defaultZOffset + 25f)
        val box = box.trimEdges(top = box.height * 0.4f)
        val bubble = chatBubble.getValue()
        useScissor(transform.asWorldCoordinateBox) {
            drawEntity(ctx, box, scale, x, y, mc.player!!, if (!bubble.shouldRemove) bubble else null)
        }
    }
}

private fun drawEntity(context: IGDrawContext, box: Box, scala: Float, mouseX: Float, mouseY: Float, entity: LivingEntity, chatBubble: ChatBubble?) {
    val x = (box.x + box.right) / 2.0f
    val y = (box.y + box.bottom) / 2.0f
    val xAngle = atan(((x - mouseX) / 40.0f).toDouble()).toFloat()
    val yAngle = atan(((y - mouseY) / 40.0f).toDouble()).toFloat()
    val rotation = Quaternionf().rotateZ(Math.PI.toFloat())
    val rotation2 = Quaternionf().rotateX(yAngle * 20.0f * (Math.PI / 180.0).toFloat())
    rotation.mul(rotation2)
    val bodyYaw = entity.bodyYaw
    val yaw = entity.yaw
    val pitch = entity.pitch
    val preHeadYaw = entity.prevHeadYaw
    val headYaw = entity.headYaw
    entity.bodyYaw = 180.0f + xAngle * 20.0f
    entity.yaw = 180.0f + xAngle * 40.0f
    entity.pitch = -yAngle * 20.0f
    entity.headYaw = entity.yaw
    entity.prevHeadYaw = entity.yaw
    val scale = entity.getScale()
    val position = Vector3f(0.0f, entity.height / 2.0f + 0.0625f * scala * scale, 0.0f)
    drawEntity(context, x, y, 50 / scale, position, rotation, rotation2, entity, chatBubble)
    entity.bodyYaw = bodyYaw
    entity.yaw = yaw
    entity.pitch = pitch
    entity.prevHeadYaw = preHeadYaw
    entity.headYaw = headYaw
}

private fun drawEntity(
    context: IGDrawContext,
    x: Float,
    y: Float,
    size: Float,
    position: Vector3f,
    rotation: Quaternionf,
    rotation2: Quaternionf,
    entity: LivingEntity,
    chatBubble: ChatBubble?
) {
    context.useMatrixStack { matrixStack ->
        matrixStack.translate(x.toDouble(), y.toDouble(), 50.0)
        matrixStack.scale(size, size, -size)
        matrixStack.translate(position.x, position.y, position.z)
        matrixStack.multiply(rotation)
        draw()
        DiffuseLighting.method_34742()
        val entityRenderDispatcher = mc.entityRenderDispatcher
        entityRenderDispatcher.rotation = rotation2.conjugate(Quaternionf()).rotateY(Math.PI.toFloat())
        entityRenderDispatcher.setRenderShadows(false)
        draw { vertexConsumers: VertexConsumerProvider ->
            entityRenderDispatcher.render(
                entity,
                0.0,
                0.0,
                0.0,
                1.0f,
                context.matrices,
                vertexConsumers,
                15728880
            )
            matrixStack.translate(0.0, 1.35, 0.0)
            chatBubble?.render(matrixStack, this.vertexConsumers, false)
        }
        draw()
        entityRenderDispatcher.setRenderShadows(true)
    }
    DiffuseLighting.enableGuiDepthLighting()
}