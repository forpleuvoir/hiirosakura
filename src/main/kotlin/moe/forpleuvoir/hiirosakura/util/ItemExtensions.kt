package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Item
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.BuiltinRegistries
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier
import java.rmi.registry.Registry
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

fun ItemStack.getEnchantmentTextWithLvl(
    context: TooltipContext,
    type: TooltipType
): List<Text> {
    return buildList {
        if (isOf(Items.ENCHANTED_BOOK))
            get(DataComponentTypes.STORED_ENCHANTMENTS)?.appendTooltip(context, { add(it.copyToText()) }, type)
        else
            get(DataComponentTypes.ENCHANTMENTS)?.appendTooltip(context, { add(it.copyToText()) }, type)
    }
}

val Item.id get() = Registries.ITEM.getId(this)

val Item.serialization get() = SerializePrimitive(id.toString())

val SerializeElement.item: Item
    get() = this.checkType<SerializePrimitive, Item> {
        Registries.ITEM.get(Identifier.of(it.asString))
    }.getOrThrow()

fun ItemStack.hasTag(tag: String): Boolean = this.streamTags().anyMatch { it.id.toString() == tag }

val ComponentType<*>.id get() = Registries.DATA_COMPONENT_TYPE.getId(this)

val ENCHANTMENT_LIST
    get() = BuiltinRegistries.createWrapperLookup().getOrThrow(RegistryKeys.ENCHANTMENT).run {
        streamKeys().map { registryKey ->
            (this.getOptional(registryKey).getOrNull() as RegistryEntry.Reference<Enchantment>)
        }.toList()
    }