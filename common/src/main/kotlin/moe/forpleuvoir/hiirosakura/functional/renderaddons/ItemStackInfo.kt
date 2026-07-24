package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.ui.configwrapper.CodeConfigWrapper
import moe.forpleuvoir.hiirosakura.util.tooltipFlag
import moe.forpleuvoir.ibukigourd.config.translateCommentKey
import moe.forpleuvoir.ibukigourd.config.translationKey
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.uiWrapper
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.ConfigItem
import moe.forpleuvoir.nebula.config.ConfigNode
import moe.forpleuvoir.nebula.config.ConfigSerde
import moe.forpleuvoir.nebula.config.item.ConfigBoolean
import moe.forpleuvoir.nebula.config.item.configBoolean
import moe.forpleuvoir.nebula.serialization.codec.Codec
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
import org.apache.commons.jexl3.MapContext
import java.util.function.Consumer

class ItemStackInfo(key: String = "item_stack_info", val enableScript: Boolean) : ConfigGroup(key) {

    private fun <T : ConfigNode> T.setTranslatedText(): T {
        translationKey = "hiirosakura.config.render_info_addon.item_stack_info.${this.name}"
        translateCommentKey = "hiirosakura.config.render_info_addon.item_stack_info.${this.name}.comment"
        return this
    }

    val script = ConfigItem(
        "script",
        """
        //example
        //if(itemStack.getCount() < 16){
        //  renderState["count"] = false;
        //}
    """.trimIndent(),
        serde = ConfigSerde.of(Codec.string)
    ).setTranslatedText().apply {
        if (enableScript) {
            addConfig(this)
        }
    }.uiWrapper { CodeConfigWrapper(it) }

    val _name by configBoolean("name", true).setTranslatedText()

    val count by configBoolean("count", true).setTranslatedText()

    val damage = configBoolean("damage", false).setTranslatedText()

    val itemId = configBoolean("item_id", false).setTranslatedText()

    val componentCount = configBoolean("component_count", false).setTranslatedText()

    val tooltipDisplay = configBoolean("tooltip_display", true).setTranslatedText()

    val tropicalFishPattern = configBoolean("tropical_fish/pattern", false).setTranslatedText()

    val instrument = configBoolean("instrument", false).setTranslatedText()

    val mapId = configBoolean("map_id", false).setTranslatedText()

    val bees = configBoolean("bees", false).setTranslatedText()

    val containerLoot = configBoolean("container_loot", false).setTranslatedText()

    val container = configBoolean("container", false).setTranslatedText()

    val bannerPatterns = configBoolean("banner_patterns", false).setTranslatedText()

    val potDecorations = configBoolean("pot_decorations", false).setTranslatedText()

    val writtenBookContent = configBoolean("written_book_content", false).setTranslatedText()

    val chargedProjectiles = configBoolean("charged_projectiles", false).setTranslatedText()

    val fireworks = configBoolean("fireworks", false).setTranslatedText()

    val fireworkExplosion = configBoolean("firework_explosion", false).setTranslatedText()

    val potionContents = configBoolean("potion_contents", false).setTranslatedText()

    val jukeboxPlayable = configBoolean("jukebox_playable", false).setTranslatedText()

    val trim = configBoolean("trim", false).setTranslatedText()

    val storedEnchantments = configBoolean("stored_enchantments", false).setTranslatedText()

    val enchantments = configBoolean("enchantments", false).setTranslatedText()

    val dyedColor = configBoolean("dyed_color", false).setTranslatedText()

    val profile = configBoolean("profile", false).setTranslatedText()

    val lore = configBoolean("lore", false).setTranslatedText()

    val attributeModifiers = configBoolean("attribute_modifiers", false).setTranslatedText()

    val unbreakable = configBoolean("unbreakable", false).setTranslatedText()

    val ominousBottleAmplifier = configBoolean("ominous_bottle_amplifier", false).setTranslatedText()

    val suspiciousStewEffects = configBoolean("suspicious_stew_effects", false).setTranslatedText()

    val blockState = configBoolean("block_state", false).setTranslatedText()

    val entityData = configBoolean("entity_data", false).setTranslatedText()

    val blockEntityData = configBoolean("block_entity_data", false).setTranslatedText()

    val canBreak = configBoolean("can_break", false).setTranslatedText()

    val canPlaceOn = configBoolean("can_place_on", false).setTranslatedText()

    val disabledItemTooltip = configBoolean("disabled_item_tooltip", false).setTranslatedText()

    val opNbtWarning = configBoolean("op_nbt_warning", false).setTranslatedText()

    private fun getRenderState(itemStack: ItemStack): Map<String, Boolean?> {
        if (!enableScript) return emptyMap()
        val renderState = mutableMapOf<String, Boolean?>()
        val scriptStr = script.getValue()
        if (script.isDefault() || scriptStr.isEmpty()) return renderState

        runCatching {
            ScriptExecutor.scriptEngine.eval(
                scriptStr, MapContext(
                    mapOf(
                        "player" to mc.player,
                        "renderState" to renderState,
                        "itemStack" to HSItemStack(itemStack)
                    )
                )
            )
        }

        return renderState
    }

    private fun isEnabled(renderState: Map<String, Boolean?>, key: String, default: Boolean): Boolean =
        if (enableScript) renderState[key] ?: default else default

    private fun <T : TooltipProvider> ItemStack.addTooltip(
        renderState: Map<String, Boolean?>,
        config: ConfigBoolean,
        component: DataComponentType<T>,
        context: Item.TooltipContext,
        tooltipDisplay: TooltipDisplay,
        tooltipFlag: TooltipFlag,
        adder: Consumer<Component>
    ) {
        if (isEnabled(renderState, config.name, config.getValue())) {
            addToTooltip(component, context, tooltipDisplay, adder, tooltipFlag)
        }
    }

    fun getItemStackInfo(
        itemStack: ItemStack,
        player: Player?,
        context: Item.TooltipContext = Item.TooltipContext.of(mc.level),
        tooltipFlag: TooltipFlag = mc.tooltipFlag
    ): List<Component> = buildList {
        val state = getRenderState(itemStack)
        val adder: Consumer<Component> = Consumer(this::add)
        // 名称和数量
        val nameAndCount = Text.empty()
        //名称
        if (isEnabled(state, "name", _name)) {
            itemStack.styledHoverName.let { nameAndCount.append(it) }
        }
        //数量
        if (isEnabled(state, "count", count) && itemStack.count > 1) {
            nameAndCount.append(" x${itemStack.count}").withStyle(ChatFormatting.WHITE)
        }
        if (nameAndCount.plainText.isNotEmpty()) this.add(nameAndCount)

        val display = if (isEnabled(state, "tooltip_display", tooltipDisplay.getValue())) {
            itemStack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT)
        } else TooltipDisplay.DEFAULT

        //热带鱼什么的
        itemStack.addTooltip(state, tropicalFishPattern, DataComponents.TROPICAL_FISH_PATTERN, context, display, tooltipFlag, adder)
        //不知道是什么 目前只有山羊角有这个
        itemStack.addTooltip(state, instrument, DataComponents.INSTRUMENT, context, display, tooltipFlag, adder)
        //地图编号
        itemStack.addTooltip(state, mapId, DataComponents.MAP_ID, context, display, tooltipFlag, adder)
        //蜜蜂
        itemStack.addTooltip(state, bees, DataComponents.BEES, context, display, tooltipFlag, adder)
        //容器战利品
        itemStack.addTooltip(state, containerLoot, DataComponents.CONTAINER_LOOT, context, display, tooltipFlag, adder)
        //容器
        itemStack.addTooltip(state, container, DataComponents.CONTAINER, context, display, tooltipFlag, adder)
        //不知道是什么
        itemStack.addTooltip(state, bannerPatterns, DataComponents.BANNER_PATTERNS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, potDecorations, DataComponents.POT_DECORATIONS, context, display, tooltipFlag, adder)
        //成书内容?
        itemStack.addTooltip(state, writtenBookContent, DataComponents.WRITTEN_BOOK_CONTENT, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, chargedProjectiles, DataComponents.CHARGED_PROJECTILES, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, fireworks, DataComponents.FIREWORKS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, fireworkExplosion, DataComponents.FIREWORK_EXPLOSION, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, potionContents, DataComponents.POTION_CONTENTS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, jukeboxPlayable, DataComponents.JUKEBOX_PLAYABLE, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, trim, DataComponents.TRIM, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, storedEnchantments, DataComponents.STORED_ENCHANTMENTS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, enchantments, DataComponents.ENCHANTMENTS, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, dyedColor, DataComponents.DYED_COLOR, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, profile, DataComponents.PROFILE, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, lore, DataComponents.LORE, context, display, tooltipFlag, adder)

        if (isEnabled(state, "attribute_modifiers", attributeModifiers.getValue()))
            itemStack.addAttributeTooltips(adder, display, player)

        if (isEnabled(
                state,
                "unbreakable",
                unbreakable.getValue()
            ) && itemStack.has(DataComponents.UNBREAKABLE) && display.shows(DataComponents.UNBREAKABLE)
        ) adder.accept(Text.translatable("item.unbreakable").withStyle(ChatFormatting.BLUE))

        itemStack.addTooltip(state, ominousBottleAmplifier, DataComponents.OMINOUS_BOTTLE_AMPLIFIER, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, suspiciousStewEffects, DataComponents.SUSPICIOUS_STEW_EFFECTS, context, display, TooltipFlag.Default(true, true), adder)
        itemStack.addTooltip(state, blockState, DataComponents.BLOCK_STATE, context, display, tooltipFlag, adder)
        itemStack.addTooltip(state, entityData, DataComponents.ENTITY_DATA, context, display, tooltipFlag, adder)

        if (isEnabled(
                state,
                "block_entity_data",
                blockEntityData.getValue()
            ) && (itemStack.`is`(Items.SPAWNER) || itemStack.`is`(Items.TRIAL_SPAWNER)) && display.shows(DataComponents.BLOCK_ENTITY_DATA)
        ) Spawner.appendHoverText(itemStack.get(DataComponents.BLOCK_ENTITY_DATA), adder, "SpawnData")

        if (isEnabled(state, "can_break", canBreak.getValue())) {
            itemStack.get(DataComponents.CAN_BREAK)?.let {
                if (display.shows(DataComponents.CAN_BREAK)) {
                    adder.accept(CommonComponents.EMPTY)
                    adder.accept(AdventureModePredicate.CAN_BREAK_HEADER)
                    it.addToTooltip(adder)
                }
            }
        }
        if (isEnabled(state, "can_place_on", canPlaceOn.getValue())) {
            itemStack.get(DataComponents.CAN_BREAK)?.let {
                if (display.shows(DataComponents.CAN_PLACE_ON)) {
                    adder.accept(CommonComponents.EMPTY)
                    adder.accept(AdventureModePredicate.CAN_BREAK_HEADER)
                    it.addToTooltip(adder)
                }
            }
        }

        if (tooltipFlag.isAdvanced) {
            if (isEnabled(state, "damage", damage.getValue()) && itemStack.isDamaged && display.shows(DataComponents.DAMAGE)) {
                adder.accept(Component.translatable("item.durability", itemStack.maxDamage - itemStack.damageValue, itemStack.maxDamage))
            }
            if (isEnabled(state, "item_id", itemId.getValue())) {
                adder.accept(Text.literal(BuiltInRegistries.ITEM.getKey(itemStack.item).toString()).withColor(-16741121))
            }
            if (isEnabled(state, "component_count", componentCount.getValue())) {
                val componentsCount: Int = itemStack.components.size()
                if (componentsCount > 0) {
                    adder.accept(Component.translatable("item.components", componentsCount).withColor(-16741121))
                }
            }
        }

        if (isEnabled(state, "disabled_item_tooltip", disabledItemTooltip.getValue())) {
            if (player != null && !itemStack.item.isEnabled(player.level().enabledFeatures()))
                adder.accept(ItemStack.DISABLED_ITEM_TOOLTIP)
        }
        if (isEnabled(state, "op_nbt_warning", opNbtWarning.getValue())) {
            if (itemStack.item.shouldPrintOpWarning(itemStack, player)) {
                ItemStack.OP_NBT_WARNING.forEach(adder)
            }
        }

    }

}