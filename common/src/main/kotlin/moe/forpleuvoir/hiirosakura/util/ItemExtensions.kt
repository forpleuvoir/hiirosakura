package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.unknownComponentType
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.enchantment.ItemEnchantments

fun ItemStack.getEnchantmentTextWithLvl(
    context: Item.TooltipContext,
    flag: TooltipFlag
): List<Component> {
    return buildList {
        if (`is`(Items.ENCHANTED_BOOK))
            get(DataComponents.STORED_ENCHANTMENTS)?.addToTooltip(context, { add(it) }, flag, this@getEnchantmentTextWithLvl.components)
        else
            get(DataComponents.ENCHANTMENTS)?.addToTooltip(context, { add(it) }, flag, this@getEnchantmentTextWithLvl.components)
    }
}

val ItemStack.allEnchantments
    get() = buildMap {
        enchantments.entrySet().forEach { entry ->
            put(entry.key, entry.intValue)
        }
        this@allEnchantments.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet().forEach { entry ->
            put(entry.key, entry.intValue)
        }
    }

val ItemStack?.empty: Boolean get() = this == null || this.isEmpty

val Item.key get() = BuiltInRegistries.ITEM.getKey(this)

val Item.serialization get() = SerializePrimitive(key.toString())

val Item.name get() = Texts.translatable(descriptionId)

val SerializeElement.asItem: Item
    get() = this.checkType<SerializePrimitive, Item> {
        BuiltInRegistries.ITEM.get(Identifier.parse(it.value.requireType())).get().value()
    }

fun ItemStack.hasTag(tag: String): Boolean = this.tags().anyMatch { it.location.toString() == tag }

val DataComponentType<*>.key get() = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(this)

val DataComponentType<*>.keyOrUnknown get() = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(this) ?: unknownComponentType

fun DataComponentType<*>.key(registryAccess: RegistryAccess) = registryAccess.lookupOrThrow(Registries.DATA_COMPONENT_TYPE).getKey(this)

fun DataComponentType<*>.keyOrUnknown(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.DATA_COMPONENT_TYPE).getKey(this) ?: unknownComponentType


