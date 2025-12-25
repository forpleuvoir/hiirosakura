package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.tooltipFlag
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.ConfigSerializable
import moe.forpleuvoir.nebula.config.item.impl.ConfigBoolean
import moe.forpleuvoir.nebula.config.item.impl.ConfigString
import moe.forpleuvoir.nebula.config.item.impl.boolean
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.level.Spawner
import java.util.function.Consumer

class ItemStackInfo(key: String = "item_stack_info", val enableScript: Boolean) : ModConfigContainer(key) {

    private fun <T : ConfigSerializable> T.setTranslatedText(): T {
        this.setUserData("#translate_text", Translatable("hiirosakura.config.render_info_addon.item_stack_info.${this.key}", fallback = this.key))
        this.setUserData("#comment", Translatable("hiirosakura.config.render_info_addon.item_stack_info.${this.key}.comment", fallback = this.key))
        return this
    }

    val script = ConfigString(
        "script",
        """
        //example
        //if(itemStack.getCount()<16){
        //  renderState["count"] = false;
        //}
    """.trimIndent()
    ).setTranslatedText().apply {
        if (enableScript) {
            addConfig(this)
        }
    }

    val name by boolean("name", false).setTranslatedText()

    val count by boolean("count", false).setTranslatedText()

    val damage = boolean("damage", false).setTranslatedText()

    val itemId = boolean("item_id", false).setTranslatedText()

    val componentCount = boolean("component_count", false).setTranslatedText()

    val tooltipDisplay = boolean("tooltip_display", true).setTranslatedText()

    val tropicalFishPattern = boolean("tropical_fish/pattern", false).setTranslatedText()

    val instrument = boolean("instrument", false).setTranslatedText()

    val mapId = boolean("map_id", false).setTranslatedText()

    val bees = boolean("bees", false).setTranslatedText()

    val containerLoot = boolean("container_loot", false).setTranslatedText()

    val container = boolean("container", false).setTranslatedText()

    val bannerPatterns = boolean("banner_patterns", false).setTranslatedText()

    val potDecorations = boolean("pot_decorations", false).setTranslatedText()

    val writtenBookContent = boolean("written_book_content", false).setTranslatedText()

    val chargedProjectiles = boolean("charged_projectiles", false).setTranslatedText()

    val fireworks = boolean("fireworks", false).setTranslatedText()

    val fireworkExplosion = boolean("firework_explosion", false).setTranslatedText()

    val potionContents = boolean("potion_contents", false).setTranslatedText()

    val jukeboxPlayable = boolean("jukebox_playable", false).setTranslatedText()

    val trim = boolean("trim", false).setTranslatedText()

    val storedEnchantments = boolean("stored_enchantments", false).setTranslatedText()

    val enchantments = boolean("enchantments", false).setTranslatedText()

    val dyedColor = boolean("dyed_color", false).setTranslatedText()

    val profile = boolean("profile", false).setTranslatedText()

    val lore = boolean("lore", false).setTranslatedText()

    val attributeModifiers = boolean("attribute_modifiers", false).setTranslatedText()

    val unbreakable = boolean("unbreakable", false).setTranslatedText()

    val ominousBottleAmplifier = boolean("ominous_bottle_amplifier", false).setTranslatedText()

    val suspiciousStewEffects = boolean("suspicious_stew_effects", false).setTranslatedText()

    val blockState = boolean("block_state", false).setTranslatedText()

    val entityData = boolean("entity_data", false).setTranslatedText()

    val blockEntityData = boolean("block_entity_data", false).setTranslatedText()

    val canBreak = boolean("can_break", false).setTranslatedText()

    val canPlaceOn = boolean("can_place_on", false).setTranslatedText()

    val disabledItemTooltip = boolean("disabled_item_tooltip", false).setTranslatedText()

    val opNbtWarning = boolean("op_nbt_warning", false).setTranslatedText()

    private fun script(itemStack: ItemStack): Map<String, Boolean?> {
        if (!enableScript) return emptyMap()
        val map = mutableMapOf<String, Boolean?>()
        if (script.getValue().isEmpty() || script.isDefault()) return map
        val executor = ScriptExecutor(script.getValue())
        executor["renderState"] = map
        executor["itemStack"] = HSItemStack(itemStack)
        executor.execute()
        return map
    }

    private fun isEnabled(map: Map<String, Boolean?>, key: String, default: Boolean): Boolean =
        if (enableScript) map[key] ?: default else default

    private fun <T : TooltipProvider> ItemStack.addTooltip(
        map: Map<String, Boolean?>,
        config: ConfigBoolean,
        component: DataComponentType<T>,
        context: Item.TooltipContext,
        tooltipDisplay: TooltipDisplay,
        tooltipFlag: TooltipFlag,
        adder: Consumer<Component>
    ) {
        if (isEnabled(map, config.key, config.getValue())) {
            addToTooltip(component, context, tooltipDisplay, adder, tooltipFlag)
        }
    }

    fun getItemStackInfo(
        itemStack: ItemStack,
        player: Player?,
        context: Item.TooltipContext = Item.TooltipContext.of(mc.level),
        tooltipFlag: TooltipFlag = mc.tooltipFlag
    ): List<Component> = buildList {
        val map = script(itemStack)
        val adder: Consumer<Component> = Consumer(this::add)
        // 名称和数量
        val nameAndCount = Text.empty()
        //名称
        if (isEnabled(map, "name", name)) {
            itemStack.styledHoverName.let { nameAndCount.append(it) }
        }
        //数量
        if (isEnabled(map, "count", count) && itemStack.count > 1) {
            nameAndCount.append(" x${itemStack.count}").withStyle(ChatFormatting.WHITE)
        }
        if (nameAndCount.plainText.isNotEmpty()) this.add(nameAndCount)

        val display = if (isEnabled(map, "tooltip_display", tooltipDisplay.getValue())) {
            itemStack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT)
        } else TooltipDisplay.DEFAULT

        //热带鱼什么的
        itemStack.addTooltip(map, tropicalFishPattern, DataComponents.TROPICAL_FISH_PATTERN, context, display, tooltipFlag, adder)
        //不知道是什么 目前只有山羊角有这个
        itemStack.addTooltip(map, instrument, DataComponents.INSTRUMENT, context, display, tooltipFlag, adder)
        //地图编号
        itemStack.addTooltip(map, mapId, DataComponents.MAP_ID, context, display, tooltipFlag, adder)
        //蜜蜂
        itemStack.addTooltip(map, bees, DataComponents.BEES, context, display, tooltipFlag, adder)
        //容器战利品
        itemStack.addTooltip(map, containerLoot, DataComponents.CONTAINER_LOOT, context, display, tooltipFlag, adder)
        //容器
        itemStack.addTooltip(map, container, DataComponents.CONTAINER, context, display, tooltipFlag, adder)
        //不知道是什么
        itemStack.addTooltip(map, bannerPatterns, DataComponents.BANNER_PATTERNS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, potDecorations, DataComponents.POT_DECORATIONS, context, display, tooltipFlag, adder)
        //成书内容?
        itemStack.addTooltip(map, writtenBookContent, DataComponents.WRITTEN_BOOK_CONTENT, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, chargedProjectiles, DataComponents.CHARGED_PROJECTILES, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, fireworks, DataComponents.FIREWORKS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, fireworkExplosion, DataComponents.FIREWORK_EXPLOSION, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, potionContents, DataComponents.POTION_CONTENTS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, jukeboxPlayable, DataComponents.JUKEBOX_PLAYABLE, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, trim, DataComponents.TRIM, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, storedEnchantments, DataComponents.STORED_ENCHANTMENTS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, enchantments, DataComponents.ENCHANTMENTS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, dyedColor, DataComponents.DYED_COLOR, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, profile, DataComponents.PROFILE, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, lore, DataComponents.LORE, context, display, tooltipFlag, adder)

        if (isEnabled(map, "attribute_modifiers", attributeModifiers.getValue()))
            itemStack.addAttributeTooltips(adder, display, player)

        if (isEnabled(
                map,
                "unbreakable",
                unbreakable.getValue()
            ) && itemStack.has(DataComponents.UNBREAKABLE) && display.shows(DataComponents.UNBREAKABLE)
        ) adder.accept(Text.translatable("item.unbreakable").withStyle(ChatFormatting.BLUE))

        itemStack.addTooltip(map, ominousBottleAmplifier, DataComponents.OMINOUS_BOTTLE_AMPLIFIER, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, suspiciousStewEffects, DataComponents.SUSPICIOUS_STEW_EFFECTS, context, display, TooltipFlag.Default(true, true), adder)
        itemStack.addTooltip(map, blockState, DataComponents.BLOCK_STATE, context, display, tooltipFlag, adder)
        itemStack.addTooltip(map, entityData, DataComponents.ENTITY_DATA, context, display, tooltipFlag, adder)

        if (isEnabled(
                map,
                "block_entity_data",
                blockEntityData.getValue()
            ) && (itemStack.`is`(Items.SPAWNER) || itemStack.`is`(Items.TRIAL_SPAWNER)) && display.shows(DataComponents.BLOCK_ENTITY_DATA)
        ) Spawner.appendHoverText(itemStack.get(DataComponents.BLOCK_ENTITY_DATA), adder, "SpawnData")

        if (isEnabled(map, "can_break", canBreak.getValue())) {
            itemStack.get(DataComponents.CAN_BREAK)?.let {
                if (display.shows(DataComponents.CAN_BREAK)) {
                    adder.accept(CommonComponents.EMPTY)
                    adder.accept(AdventureModePredicate.CAN_BREAK_HEADER)
                    it.addToTooltip(adder)
                }
            }
        }
        if (isEnabled(map, "can_place_on", canPlaceOn.getValue())) {
            itemStack.get(DataComponents.CAN_BREAK)?.let {
                if (display.shows(DataComponents.CAN_PLACE_ON)) {
                    adder.accept(CommonComponents.EMPTY)
                    adder.accept(AdventureModePredicate.CAN_BREAK_HEADER)
                    it.addToTooltip(adder)
                }
            }
        }

        if (tooltipFlag.isAdvanced) {
            if (isEnabled(map, "damage", damage.getValue()) && itemStack.isDamaged && display.shows(DataComponents.DAMAGE)) {
                adder.accept(Component.translatable("item.durability", itemStack.maxDamage - itemStack.damageValue, itemStack.maxDamage))
            }
            if (isEnabled(map, "item_id", itemId.getValue())) {
                adder.accept(Text.literal(BuiltInRegistries.ITEM.getKey(itemStack.item).toString()).withColor(-16741121))
            }
            if (isEnabled(map, "component_count", componentCount.getValue())) {
                val componentsCount: Int = itemStack.components.size()
                if (componentsCount > 0) {
                    adder.accept(Component.translatable("item.components", componentsCount).withColor(-16741121))
                }
            }
        }

        if (isEnabled(map, "disabled_item_tooltip", disabledItemTooltip.getValue())) {
            if (player != null && !itemStack.item.isEnabled(player.level().enabledFeatures()))
                adder.accept(ItemStack.DISABLED_ITEM_TOOLTIP)
        }
        if (isEnabled(map, "op_nbt_warning", opNbtWarning.getValue())) {
            if (itemStack.item.shouldPrintOpWarning(itemStack, player)) {
                ItemStack.OP_NBT_WARNING.forEach(adder)
            }
        }

    }

}