package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.config.RenderInfoAddon
import moe.forpleuvoir.hiirosakura.config.RenderInfoAddon.DropEntity.onlyYRotation
import moe.forpleuvoir.hiirosakura.util.tooltipType
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.McText
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState
import net.minecraft.client.render.entity.state.ItemEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.ExperienceOrbEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.BlockPredicatesChecker
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Item.TooltipContext
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.Formatting
import net.minecraft.util.math.RotationAxis
import org.joml.Vector3f
import java.util.function.Consumer

object EntityRenderAddon {

    private val config = RenderInfoAddon.DropEntity

    @JvmStatic
    var currentItemEntity: ItemEntity? = null

    @JvmStatic
    fun renderItemEntityInfo(
        itemEntity: ItemEntity,
        textRenderer: TextRenderer,
        dispatcher: EntityRenderDispatcher,
        state: ItemEntityRenderState,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider.Immediate,
        light: Int
    ) {
        if (RenderInfoAddon.DropEntity.distance <= 0) return
        if (dispatcher.getSquaredDistanceToCamera(itemEntity) > RenderInfoAddon.DropEntity.distance) return
        val texts = getItemEntityRenderText(itemEntity, state)
        if (texts.isNotEmpty()) {
            renderEntityMultiText(
                itemEntity.height, texts, dispatcher, textRenderer, matrixStack, vertexConsumerProvider, light
            )
        }
    }

    private fun getItemEntityRenderText(itemEntity: ItemEntity, state: ItemEntityRenderState): List<Text> {
        val context = TooltipContext.DEFAULT

        val stack = itemEntity.stack
        val type = mc.tooltipType
        return buildList {
            // 名称和数量
            val nameAndCount = Text.empty()
            //名称
            if (config.name.value) {
                stack.formattedName?.let { nameAndCount.append(it) }
            }
            //数量
            if (config.count.value && stack.count > 1) {
                nameAndCount.append(" x${stack.count}")
            }
            if (nameAndCount.plainText.isNotEmpty()) this.add(nameAndCount.copyToText())
            //地图编号
            if (config.mapId.value && !type.isAdvanced && !stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                val mapIdComponent = stack.get(DataComponentTypes.MAP_ID)
                if (mapIdComponent != null) {
                    this.add(FilledMapItem.getIdText(mapIdComponent).copyToText())
                }
            }
            val consumer: Consumer<McText> = Consumer { this.add(it.copyToText()) }
            //其他工具提示
            if (config.additionalTooltip.value && !stack.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)) {
                val l = mutableListOf<McText>()
                stack.item.appendTooltip(stack, context, l, type)
                this.addAll(l.map { it.copyToText() })
            }
            //可播放的唱片
            if (config.jukeboxPlayable.value) {
                stack.get(DataComponentTypes.JUKEBOX_PLAYABLE)?.appendTooltip(context, consumer, type)
            }
            //盔甲装饰
            if (config.trim.value) {
                stack.get(DataComponentTypes.TRIM)?.appendTooltip(context, consumer, type)
            }
            //储存的附魔
            if (config.storedEnchantments.value) {
                stack.get(DataComponentTypes.STORED_ENCHANTMENTS)?.appendTooltip(context, consumer, type)
            }
            //附魔
            if (config.enchantments.value) {
                stack.get(DataComponentTypes.ENCHANTMENTS)?.appendTooltip(context, consumer, type)
            }
            //染色颜色
            if (config.dyedColor.value) {
                stack.get(DataComponentTypes.DYED_COLOR)?.appendTooltip(context, consumer, type)
            }
            if (config.lore.value) {
                stack.get(DataComponentTypes.LORE)?.appendTooltip(context, consumer, type)
            }
            //属性修饰符
            if (config.attributeModifiers.value) {
                stack.appendAttributeModifiersTooltip(consumer, mc.player)
            }
            //不可破坏
            if (config.unbreakable.value) {
                stack.get(DataComponentTypes.UNBREAKABLE)?.appendTooltip(context, consumer, type)
            }
            //不祥之兆
            if (config.ominousBottleAmplifier.value) {
                stack.get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)?.appendTooltip(context, consumer, type)
            }
            //可疑的炖菜
            if (config.suspiciousStewEffect.value) {
                stack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)?.appendTooltip(context, consumer, type)
            }
            //可破坏
            if (config.canBreak.value) {
                stack.get(DataComponentTypes.CAN_BREAK)?.let { blockPredicatesChecker ->
                    if (blockPredicatesChecker.showInTooltip()) {
                        consumer.accept(ScreenTexts.EMPTY);
                        consumer.accept(BlockPredicatesChecker.CAN_BREAK_TEXT);
                        blockPredicatesChecker.addTooltips(consumer);
                    }
                }
            }
            //可放置于
            if (config.canPlaceOn.value) {
                stack.get(DataComponentTypes.CAN_PLACE_ON)?.let { blockPredicatesChecker ->
                    if (blockPredicatesChecker.showInTooltip()) {
                        consumer.accept(ScreenTexts.EMPTY);
                        consumer.accept(BlockPredicatesChecker.CAN_PLACE_TEXT);
                        blockPredicatesChecker.addTooltips(consumer);
                    }
                }
            }
            if (type.isAdvanced) {
                //耐久度
                if (config.durability.value && stack.isDamaged) {
                    add(Text.translatable("item.durability", fallback = null, (stack.maxDamage - stack.damage), stack.maxDamage))
                }
                //物品id
                if (config.itemId.value) {
                    add(Literal(Registries.ITEM.getId(stack.item).toString()).formatted(Formatting.DARK_GRAY))
                }
                //组件
                if (config.components.value) {
                    val i = stack.components.size()
                    if (i > 0) add(Text.translatable("item.components", fallback = null, i).formatted(Formatting.DARK_GRAY))
                }
            }
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
        state: ExperienceOrbEntityRenderState,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider.Immediate,
        light: Int
    ) {
        if (!config.experienceOrbValue.value) return
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
        val backgroundColor = (alpha * 255.0f).toInt() shl 24
        val x = (-textRenderer.getWidth(text) / 2).toFloat()
        textRenderer.draw(
            text, x, 0f, -1, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL,
            backgroundColor, light
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




