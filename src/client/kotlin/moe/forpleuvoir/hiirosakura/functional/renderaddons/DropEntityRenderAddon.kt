package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.item.impl.double
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.ExperienceOrbEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.util.math.RotationAxis
import org.joml.Vector3f

object DropEntityRenderAddon : ModConfigContainer("drop_entity") {

    val enable by keyBindBoolean("enable", value = false)

    val distance by double("distance", 233.0, 0.0, 2333.0)

    val onlyYRotation by keyBindBoolean("only_y_rotation", value = true)

    val experienceOrbValue by keyBindBoolean("experience_orb_value", value = false)

    val itemStackInfo = addConfig(ItemStackInfo())

    @JvmStatic
    var currentItemEntity: ItemEntity? = null

    @JvmStatic
    fun renderItemEntityInfo(
        itemEntity: ItemEntity,
        textRenderer: TextRenderer,
        dispatcher: EntityRenderDispatcher,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider.Immediate,
        light: Int
    ) {
        if (!enable.value || distance <= 0) return
        if (dispatcher.getSquaredDistanceToCamera(itemEntity) > distance) return
        val texts = itemStackInfo.getItemStackInfo(itemEntity.stack)
        if (texts.isNotEmpty()) {
            renderEntityMultiText(
                itemEntity.height, texts, dispatcher, textRenderer, matrixStack, vertexConsumerProvider, light
            )
        }
    }


    @JvmStatic
    var currentExperienceOrbEntity: ExperienceOrbEntity? = null

    @JvmStatic
    fun renderExperienceOrbValue(
        color: Int,
        entity: ExperienceOrbEntity,
        textRenderer: TextRenderer,
        dispatcher: EntityRenderDispatcher,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider.Immediate,
        light: Int
    ) {
        if (!enable.value || !experienceOrbValue.value || distance <= 0) return
        val text = Literal(entity.experienceAmount.toString()).withColor(Color(color))
        renderEntityText(entity.height, -0.3, text, dispatcher, textRenderer, matrixStack, vertexConsumerProvider, light)
    }

    /**
     * 在实体上方渲染文本
     *
     * @param height                 目标实体高度
     * @param textHeight             高度
     * @param text                   需要渲染的文本
     * @param dispatcher             [EntityRenderDispatcher]
     * @param textRenderer           [TextRenderer]
     * @param matrixStack            [MatrixStack]
     * @param vertexConsumerProvider [VertexConsumerProvider]
     * @param light                  亮度
     */
    private fun renderEntityText(
        height: Float,
        textHeight: Double,
        text: Text,
        dispatcher: EntityRenderDispatcher,
        textRenderer: TextRenderer,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int
    ) {
        val height = height + 0.5f + textHeight
        matrixStack.push()
        matrixStack.translate(0.0, height, 0.0)
        if (onlyYRotation.value)
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(dispatcher.rotation.getEulerAnglesYXZ(Vector3f()).y()))
        else
            matrixStack.multiply(dispatcher.rotation)
        matrixStack.scale(0.025f, -0.025f, 0.025f)
        val matrix4f = matrixStack.peek().positionMatrix
        val alpha = mc.options.getTextBackgroundOpacity(0.25f)
        val backgroundColor = if (text.plainText.isEmpty()) 0 else (alpha * 255.0f).toInt() shl 24
        val x = (-text.width / 2)
        textRenderer.draw(
            text, x, 0f, -1, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL,
            if (text.string.isEmpty()) 0 else backgroundColor, light
        )
        matrixStack.pop()
    }

    /**
     * 在实体上方渲染多行文本
     *
     * @param height                 目标实体高度
     * @param text                   需要渲染的文本列表
     * @param dispatcher             [EntityRenderDispatcher]
     * @param textRenderer           [TextRenderer]
     * @param matrixStack            [MatrixStack]
     * @param vertexConsumerProvider [VertexConsumerProvider]
     * @param light                  亮度
     */
    fun renderEntityMultiText(
        height: Float,
        text: List<Text>,
        dispatcher: EntityRenderDispatcher,
        textRenderer: TextRenderer,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int
    ) {
        val textRows = text.size
        var textHeight = (textRows * 0.25f).toDouble()
        for (item in text) {
            textHeight -= 0.25
            renderEntityText(
                height, textHeight, item, dispatcher, textRenderer,
                matrixStack, vertexConsumerProvider, light
            )
        }
    }

}




