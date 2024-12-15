package moe.forpleuvoir.hiirosakura.config

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.nebula.config.item.impl.double

object RenderInfoAddon : ModConfigContainer("render_info_addon") {

    val showEnchantmentWhenSwitch by keyBindBoolean("show_enchantment_when_switch", value = false, keyBind = KeyBind())

    private val _itemEntity = addConfig(ItemEntity)

    object ItemEntity : ModConfigContainer("item_entity") {

        val distance by double("distance", 50.0, 0.0, 999.0)

        val onlyYRotation by keyBindBoolean("only_y_rotation", value = true, keyBind = KeyBind())

        val name by keyBindBoolean("name", value = false, keyBind = KeyBind())

        val count by keyBindBoolean("count", value = false, keyBind = KeyBind())

        val mapId by keyBindBoolean("map_id", value = false, keyBind = KeyBind())

        val additionalTooltip by keyBindBoolean("additional_tooltip", value = false, keyBind = KeyBind())

        val jukeboxPlayable by keyBindBoolean("jukebox_playable", value = false, keyBind = KeyBind())

        val trim by keyBindBoolean("trim", value = false, keyBind = KeyBind())

        val storedEnchantments by keyBindBoolean("stored_enchantments", value = false, keyBind = KeyBind())

        val enchantments by keyBindBoolean("enchantments", value = false, keyBind = KeyBind())

        val dyedColor by keyBindBoolean("dyed_color", value = false, keyBind = KeyBind())

        val lore by keyBindBoolean("lore", value = false, keyBind = KeyBind())

        val attributeModifiers by keyBindBoolean("attribute_modifiers", value = false, keyBind = KeyBind())

        val unbreakable by keyBindBoolean("unbreakable", value = false, keyBind = KeyBind())

        val ominousBottleAmplifier by keyBindBoolean("ominous_bottle_amplifier", value = false, keyBind = KeyBind())

        val suspiciousStewEffect by keyBindBoolean("suspicious_stew_effect", value = false, keyBind = KeyBind())

        val canBreak by keyBindBoolean("can_break", value = false, keyBind = KeyBind())

        val canPlaceOn by keyBindBoolean("can_place_on", value = false, keyBind = KeyBind())

        val durability by keyBindBoolean("durability", value = false, keyBind = KeyBind())

        val itemId by keyBindBoolean("item_id", value = false, keyBind = KeyBind())

        val components by keyBindBoolean("components", value = false, keyBind = KeyBind())

    }

}