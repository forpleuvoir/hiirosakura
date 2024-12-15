package moe.forpleuvoir.hiirosakura.config

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.double
import moe.forpleuvoir.nebula.config.item.impl.enum

object RenderInfoAddon : ModConfigContainer("render_info_addon") {

    val showEnchantmentWhenSwitch by keyBindBoolean("show_enchantment_when_switch", value = false)

    private val _itemEntity = addConfig(ItemEntity)

    object ItemEntity : ModConfigContainer("item_entity") {

        val distance by double("distance", 50.0, 0.0, 999.0)

        val onlyYRotation by keyBindBoolean("only_y_rotation", value = true)

        val name by keyBindBoolean("name", value = false)

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

    }

    private val _tnt = addConfig(Tnt)

    object Tnt : ModConfigContainer("tnt") {

        enum class RenderType {
            Text, Box, None
        }

        val renderType by enum("tnt_fuse", RenderType.None)

        val onlyYRotation by keyBindBoolean("only_y_rotation", value = false)

    }

}