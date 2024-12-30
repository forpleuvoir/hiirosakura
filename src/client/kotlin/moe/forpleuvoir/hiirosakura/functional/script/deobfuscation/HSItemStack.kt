@file:Suppress("unused")

package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.hiirosakura.util.tooltipType
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

class HSItemStack(private val stack: ItemStack) {

    companion object {
        @JvmStatic
        fun fromItemStack(stack: ItemStack) = HSItemStack(stack)
    }

    fun getCount(): Int = stack.count

    fun getItem() = Registries.ITEM.getId(stack.item).toString()

    fun getName(): String = stack.name.string

    fun getItemName(): String = stack.itemName.string

    fun getDurability() = stack.damage

    fun getMaxDurability() = stack.maxDamage

    fun getDamagePercent() = stack.damage.toFloat() / stack.maxDamage

    fun getRarity(): String = stack.getRarity().asString()

    fun isEnchantable(): Boolean = stack.isEnchantable

    fun getEnchantments() = buildList<String> {
        stack.get(DataComponentTypes.ENCHANTMENTS)?.appendTooltip(TooltipContext.DEFAULT, {
            add(it.string)
        }, mc.tooltipType)
    }

    fun getStoredEnchantments() = buildList<String> {
        stack.get(DataComponentTypes.STORED_ENCHANTMENTS)?.appendTooltip(TooltipContext.DEFAULT, {
            add(it.string)
        }, mc.tooltipType)
    }

}