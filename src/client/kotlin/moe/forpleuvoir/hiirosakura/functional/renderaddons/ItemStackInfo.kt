package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.tooltipType
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.ConfigSerializable
import moe.forpleuvoir.nebula.config.item.impl.string
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.BlockPredicatesChecker
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.Formatting
import java.util.function.Consumer

class ItemStackInfo(key: String = "item_stack_info") : ModConfigContainer(key) {

    private fun <T : ConfigSerializable> T.setTranslatedText(): T {
        this.setUserData("#translate_text", Translatable("hiirosakura.config.render_info_addon.item_stack_info.${this.key}"))
        this.setUserData("#comment", Translatable("hiirosakura.config.render_info_addon.item_stack_info.${this.key}.comment"))
        return this
    }

    val script = string(
        "script",
        """
        //example
        //if(itemStack.getCount()<16){
        //  renderer["count"] = false
        //}
    """.trimIndent()
    ).setTranslatedText()

    val name by keyBindBoolean("name", value = false).setTranslatedText()

    val count by keyBindBoolean("count", value = false).setTranslatedText()

    val mapId by keyBindBoolean("map_id", value = false).setTranslatedText()

    val additionalTooltip by keyBindBoolean("additional_tooltip", value = false).setTranslatedText()

    val jukeboxPlayable by keyBindBoolean("jukebox_playable", value = false).setTranslatedText()

    val trim by keyBindBoolean("trim", value = false).setTranslatedText()

    val storedEnchantments by keyBindBoolean("stored_enchantments", value = false).setTranslatedText()

    val enchantments by keyBindBoolean("enchantments", value = false).setTranslatedText()

    val dyedColor by keyBindBoolean("dyed_color", value = false).setTranslatedText()

    val lore by keyBindBoolean("lore", value = false).setTranslatedText()

    val attributeModifiers by keyBindBoolean("attribute_modifiers", value = false).setTranslatedText()

    val unbreakable by keyBindBoolean("unbreakable", value = false).setTranslatedText()

    val ominousBottleAmplifier by keyBindBoolean("ominous_bottle_amplifier", value = false).setTranslatedText()

    val suspiciousStewEffect by keyBindBoolean("suspicious_stew_effect", value = false).setTranslatedText()

    val canBreak by keyBindBoolean("can_break", value = false).setTranslatedText()

    val canPlaceOn by keyBindBoolean("can_place_on", value = false).setTranslatedText()

    val durability by keyBindBoolean("durability", value = false).setTranslatedText()

    val itemId by keyBindBoolean("item_id", value = false).setTranslatedText()

    val components by keyBindBoolean("components", value = false).setTranslatedText()

    private fun script(itemStack: ItemStack): Map<String, Boolean?> {
        val map = mutableMapOf<String, Boolean?>()
        if (script.getValue().isEmpty() || script.isDefault()) return map
        val executor = ScriptExecutor(script.getValue())
        executor["renderer"] = map
        executor["itemStack"] = HSItemStack(itemStack)
        executor.execute()
        return map
    }

    private fun isEnabled(map: Map<String, Boolean?>, key: String, default: Boolean): Boolean = map[key] ?: default

    fun getItemStackInfo(
        itemStack: ItemStack,
        context: TooltipContext = TooltipContext.DEFAULT,
        tooltipType: TooltipType = mc.tooltipType
    ): List<Text> = buildList {
        val map = script(itemStack)

        // 名称和数量
        val nameAndCount = Text.empty()
        //名称
        if (isEnabled(map, "name", name.value)) {
            itemStack.formattedName?.let { nameAndCount.append(it) }
        }
        //数量
        if (isEnabled(map, "count", count.value) && itemStack.count > 1) {
            nameAndCount.append(" x${itemStack.count}")
        }
        if (nameAndCount.plainText.isNotEmpty()) this.add(nameAndCount.copyToText())
        //地图编号
        if (isEnabled(map, "map_id", mapId.value) && !tooltipType.isAdvanced && !itemStack.contains(DataComponentTypes.CUSTOM_NAME)) {
            val mapIdComponent = itemStack.get(DataComponentTypes.MAP_ID)
            if (mapIdComponent != null) {
                this.add(FilledMapItem.getIdText(mapIdComponent).copyToText())
            }
        }
        val consumer: Consumer<McText> = Consumer { this.add(it.copyToText()) }
        //附加的工具提示
        if (isEnabled(map, "additional_tooltip", additionalTooltip.value) && !itemStack.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)) {
            val l = mutableListOf<McText>()
            itemStack.item.appendTooltip(itemStack, context, l, tooltipType)
            this.addAll(l.map { it.copyToText() })
        }
        //可播放的唱片
        if (isEnabled(map, "jukebox_playable", jukeboxPlayable.value)) {
            itemStack.get(DataComponentTypes.JUKEBOX_PLAYABLE)?.appendTooltip(context, consumer, tooltipType)
        }
        //盔甲装饰
        if (isEnabled(map, "trim", trim.value)) {
            itemStack.get(DataComponentTypes.TRIM)?.appendTooltip(context, consumer, tooltipType)
        }
        //储存的附魔
        if (isEnabled(map, "stored_enchantments", storedEnchantments.value)) {
            itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)?.appendTooltip(context, consumer, tooltipType)
        }
        //附魔
        if (isEnabled(map, "enchantments", enchantments.value)) {
            itemStack.get(DataComponentTypes.ENCHANTMENTS)?.appendTooltip(context, consumer, tooltipType)
        }
        //染色颜色
        if (isEnabled(map, "dyed_color", dyedColor.value)) {
            itemStack.get(DataComponentTypes.DYED_COLOR)?.appendTooltip(context, consumer, tooltipType)
        }
        //Lore
        if (isEnabled(map, "lore", lore.value)) {
            itemStack.get(DataComponentTypes.LORE)?.appendTooltip(context, consumer, tooltipType)
        }
        //属性修饰符
        if (isEnabled(map, "attribute_modifiers", attributeModifiers.value)) {
            itemStack.appendAttributeModifiersTooltip(consumer, mc.player)
        }
        //不可破坏
        if (isEnabled(map, "unbreakable", unbreakable.value)) {
            itemStack.get(DataComponentTypes.UNBREAKABLE)?.appendTooltip(context, consumer, tooltipType)
        }
        //不祥之兆
        if (isEnabled(map, "ominous_bottle_amplifier", ominousBottleAmplifier.value)) {
            itemStack.get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)?.appendTooltip(context, consumer, tooltipType)
        }
        //可疑的炖菜
        if (isEnabled(map, "suspicious_stew_effect", suspiciousStewEffect.value)) {
            itemStack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)?.appendTooltip(context, consumer, tooltipType)
        }
        //可破坏
        if (isEnabled(map, "can_break", canBreak.value)) {
            itemStack.get(DataComponentTypes.CAN_BREAK)?.let { blockPredicatesChecker ->
                if (blockPredicatesChecker.showInTooltip()) {
                    consumer.accept(ScreenTexts.EMPTY)
                    consumer.accept(BlockPredicatesChecker.CAN_BREAK_TEXT)
                    blockPredicatesChecker.addTooltips(consumer)
                }
            }
        }
        //可放置于
        if (isEnabled(map, "can_place_on", canPlaceOn.value)) {
            itemStack.get(DataComponentTypes.CAN_PLACE_ON)?.let { blockPredicatesChecker ->
                if (blockPredicatesChecker.showInTooltip()) {
                    consumer.accept(ScreenTexts.EMPTY)
                    consumer.accept(BlockPredicatesChecker.CAN_PLACE_TEXT)
                    blockPredicatesChecker.addTooltips(consumer)
                }
            }
        }
        if (tooltipType.isAdvanced) {
            //耐久度
            if (isEnabled(map, "durability", durability.value) && itemStack.isDamaged) {
                add(Text.translatable("item.durability", fallback = null, (itemStack.maxDamage - itemStack.damage), itemStack.maxDamage))
            }
            //物品id
            if (isEnabled(map, "item_id", itemId.value)) {
                add(Literal(Registries.ITEM.getId(itemStack.item).toString()).formatted(Formatting.DARK_GRAY))
            }
            //组件
            if (isEnabled(map, "components", components.value)) {
                val i = itemStack.components.size()
                if (i > 0) add(Text.translatable("item.components", fallback = null, i).formatted(Formatting.DARK_GRAY))
            }
        }
    }

}