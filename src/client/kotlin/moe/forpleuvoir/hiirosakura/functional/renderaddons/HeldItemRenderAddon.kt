package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.util.tooltipType
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.vector2f
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderBox
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderText
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.IGDrawContext
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.math.Vector2f
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.float
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.BlockPredicatesChecker
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenTexts
import java.util.function.Consumer

object HeldItemRenderAddon : ModConfigContainer("held_item") {

    val enable by keyBindBoolean("enable", value = false)

    val offset by vector2f("offset", Vector2f(0f, 0f), Vector2f(-200f, -200f), Vector2f(200f, 200f))

    val spacing by float("spacing", 1f, 0f, 10f)

    val shadow by boolean("shadow", true)

    val textBackground by color("text_background", Colors.BLACK.alpha(0f))

    val overallBackground by color("overall_background", Colors.BLACK.alpha(0f))

    val name by keyBindBoolean("name", value = true)

    val count by keyBindBoolean("count", value = false)

    val mapId by keyBindBoolean("map_id", value = false)

    val additionalTooltip by keyBindBoolean("additional_tooltip", value = false)

    val jukeboxPlayable by keyBindBoolean("jukebox_playable", value = false)

    val trim by keyBindBoolean("trim", value = false)

    val storedEnchantments by keyBindBoolean("stored_enchantments", value = false)

    val enchantments by keyBindBoolean("enchantments", value = false)

    val dyedColor by keyBindBoolean("dyed_color", value = false)

    val lore by keyBindBoolean("lore", value = false)

    val attributeModifiers by keyBindBoolean("attribute_modifiers", value = false)

    val unbreakable by keyBindBoolean("unbreakable", value = false)

    val ominousBottleAmplifier by keyBindBoolean("ominous_bottle_amplifier", value = false)

    val suspiciousStewEffect by keyBindBoolean("suspicious_stew_effect", value = false)

    val canBreak by keyBindBoolean("can_break", value = false)

    val canPlaceOn by keyBindBoolean("can_place_on", value = false)

    val durability by keyBindBoolean("durability", value = false)

    val itemId by keyBindBoolean("item_id", value = false)

    val components by keyBindBoolean("components", value = false)

    @JvmStatic
    fun shouldRender(new: ItemStack, origin: ItemStack): Boolean {
        return enable.value && !ItemStack.areEqual(new, origin)
    }

    private fun getItemStackRenderText(stack: ItemStack): List<Text> {
        val context = TooltipContext.DEFAULT
        val type = mc.tooltipType
        return buildList {
            // 名称和数量
            val nameAndCount = Text.empty()
            //名称
            if (name.value) {
                stack.formattedName?.let { nameAndCount.append(it) }
            }
            //数量
            if (count.value && stack.count > 1) {
                nameAndCount.append(" x${stack.count}")
            }
            if (nameAndCount.plainText.isNotEmpty()) this.add(nameAndCount.copyToText())
            //地图编号
            if (mapId.value && !type.isAdvanced && !stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                val mapIdComponent = stack.get(DataComponentTypes.MAP_ID)
                if (mapIdComponent != null) {
                    this.add(FilledMapItem.getIdText(mapIdComponent).copyToText())
                }
            }
            val consumer: Consumer<McText> = Consumer { this.add(it.copyToText()) }
            //其他工具提示
            if (additionalTooltip.value && !stack.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)) {
                val l = mutableListOf<McText>()
                stack.item.appendTooltip(stack, context, l, type)
                this.addAll(l.map { it.copyToText() })
            }
            //可播放的唱片
            if (jukeboxPlayable.value) {
                stack.get(DataComponentTypes.JUKEBOX_PLAYABLE)?.appendTooltip(context, consumer, type)
            }
            //盔甲装饰
            if (trim.value) {
                stack.get(DataComponentTypes.TRIM)?.appendTooltip(context, consumer, type)
            }
            //储存的附魔
            if (storedEnchantments.value) {
                stack.get(DataComponentTypes.STORED_ENCHANTMENTS)?.appendTooltip(context, consumer, type)
            }
            //附魔
            if (enchantments.value) {
                stack.get(DataComponentTypes.ENCHANTMENTS)?.appendTooltip(context, consumer, type)
            }
            //染色颜色
            if (dyedColor.value) {
                stack.get(DataComponentTypes.DYED_COLOR)?.appendTooltip(context, consumer, type)
            }
            if (lore.value) {
                stack.get(DataComponentTypes.LORE)?.appendTooltip(context, consumer, type)
            }
            //属性修饰符
            if (attributeModifiers.value) {
                stack.appendAttributeModifiersTooltip(consumer, mc.player)
            }
            //不可破坏
            if (unbreakable.value) {
                stack.get(DataComponentTypes.UNBREAKABLE)?.appendTooltip(context, consumer, type)
            }
            //不祥之兆
            if (ominousBottleAmplifier.value) {
                stack.get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)?.appendTooltip(context, consumer, type)
            }
            //可疑的炖菜
            if (suspiciousStewEffect.value) {
                stack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)?.appendTooltip(context, consumer, type)
            }
            //可破坏
            if (canBreak.value) {
                stack.get(DataComponentTypes.CAN_BREAK)?.let { blockPredicatesChecker ->
                    if (blockPredicatesChecker.showInTooltip()) {
                        consumer.accept(ScreenTexts.EMPTY)
                        consumer.accept(BlockPredicatesChecker.CAN_BREAK_TEXT)
                        blockPredicatesChecker.addTooltips(consumer)
                    }
                }
            }
            //可放置于
            if (canPlaceOn.value) {
                stack.get(DataComponentTypes.CAN_PLACE_ON)?.let { blockPredicatesChecker ->
                    if (blockPredicatesChecker.showInTooltip()) {
                        consumer.accept(ScreenTexts.EMPTY)
                        consumer.accept(BlockPredicatesChecker.CAN_PLACE_TEXT)
                        blockPredicatesChecker.addTooltips(consumer)
                    }
                }
            }
            if (type.isAdvanced) {
                //耐久度
                if (durability.value && stack.isDamaged) {
                    add(Text.translatable("item.durability", fallback = null, (stack.maxDamage - stack.damage), stack.maxDamage))
                }
                //物品id
                if (itemId.value) {
                    add(Literal(Registries.ITEM.getId(stack.item).toString()).withColor(Color(10, 136, 226)))
                }
                //组件
                if (components.value) {
                    val i = stack.components.size()
                    if (i > 0) add(Text.translatable("item.components", fallback = null, i).withColor(Color(10, 136, 226)))
                }
            }
        }
    }

    @JvmStatic
    internal fun render(context: IGDrawContext, textRenderer: TextRenderer, y: Int, alpha: Int, itemStack: ItemStack) {
        val texts = getItemStackRenderText(itemStack)
        if (texts.isEmpty()) return
        val maxHeight = texts.size * (textRenderer.fontHeight + spacing)
        val maxWidth = texts.maxWidth
        val box = Box(
            x = (context.scaledWindowWidth - maxWidth) / 2f,
            y = y.toFloat() - maxHeight,
            width = maxWidth,
            height = maxHeight
        )
        context.useMatrixStack {
            it.translate(offset.x(), offset.y(), 0f)
            if (overallBackground.alpha > 5) {
                batchRenderBox {
                    pushRoundBox(box.expandEdges(4f), overallBackground.opacity(alpha), 2)
                }
            }
            batchRenderText(textRenderer) {
                val verticalOffsets = Arrangement.spacedBy(spacing).arrange(box.width, List(texts.size) { textRenderer.fontHeight.toFloat() })
                val horizontalOffsets = texts.map { Alignment.CenterHorizontally.align(box.width, it.width) }
                horizontalOffsets.zip(verticalOffsets) { x, y ->
                    Vector2f(box.x + x, box.y + y)
                }.forEachIndexed { index, offset ->
                    val text = texts[index]
                    pushText(
                        text,
                        offset.x,
                        offset.y,
                        shadow,
                        TextLayerType.NORMAL,
                        Color(text.style.color?.rgb ?: 0xAAAAAA).alpha(alpha),
                        if (text.string.isEmpty()) Colors.BLACK.alpha(0) else textBackground.opacity(alpha),
                        LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        textRenderer.isRightToLeft
                    )
                }
            }
        }

    }

}



