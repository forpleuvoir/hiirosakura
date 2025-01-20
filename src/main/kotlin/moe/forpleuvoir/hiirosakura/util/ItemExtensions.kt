package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.copyToText
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.Registries
import java.util.function.Consumer

fun ItemStack.getEnchantmentTextWithLvl(
    context: TooltipContext,
    type: TooltipType
): List<Text> {
    return buildList {
        get(DataComponentTypes.ENCHANTMENTS)?.appendTooltip(context, Consumer { add(it.copyToText()) }, type)
        get(DataComponentTypes.STORED_ENCHANTMENTS)?.appendTooltip(context, Consumer { add(it.copyToText()) }, type)
    }
}

val Item.id get() = Registries.ITEM.getId(this)