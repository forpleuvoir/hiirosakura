@file:Suppress("unused")

package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.hiirosakura.util.tooltipFlag
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.*
import kotlin.jvm.optionals.getOrNull

/**
 * HSItemStack 类用于封装 Minecraft 中的物品堆栈(ItemStack)对象，并提供相关的属性和辅助方法以获取堆栈信息。
 *
 * @property vanilla 被封装的物品堆栈对象。
 */
class HSItemStack(@JvmField val vanilla: ItemStack) {

    companion object {
        @JvmStatic
        fun fromItemStack(stack: ItemStack) = HSItemStack(stack)
    }

    /**
     * 获取堆栈中的物品数量。
     *
     * @return 一个整数，表示堆栈中物品的数量。
     */
    fun getCount(): Int = vanilla.count

    fun getMaxCount(): Int = vanilla.maxStackSize

    fun isStackable() = vanilla.isStackable

    fun hasComponent(componentType: String): Boolean = vanilla.has(BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.parse(componentType)).get().value())

    fun getComponent(componentType: String): Any? {
        return BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.parse(componentType)).getOrNull()?.value()?.let {
            vanilla.get(it)
        }
    }

    /**
     * 获取当前物品的注册名。
     *
     * 通过 `BuiltInRegistries.ITEM` 取得该物品的标识符，返回 `命名空间:路径` 形式（例如
     * `minecraft:diamond_sword`）。
     *
     * @return 当前物品注册名的字符串形式。
     */
    fun getItem() = BuiltInRegistries.ITEM.getKey(vanilla.item).toString()

    fun getTags(): List<String> = vanilla.tags().map { it.location.toString() }.toList()

    fun hasTag(tag: String) = vanilla.tags().anyMatch { it.location.toString() == tag }

    /**
     * 获取物品的名称。
     *
     * @return 表示物品名称的字符串。
     */
    fun getName(): String = vanilla.hoverName.string

    /**
     * 获取物品名称的字符串表示。
     *
     * @return 当前物品名称的字符串形式。
     */
    fun getItemName(): String = vanilla.itemName.string


    fun isShovel() = vanilla.item is ShovelItem

    fun isHoe() = vanilla.item is HoeItem

    fun isAxe() = vanilla.item is AxeItem

    fun isShield() = vanilla.item is ShieldItem

    fun isBlock() = vanilla.item is BlockItem

    fun isDamageable() = vanilla.isDamageableItem

    /**
     * 获取物品耐久值（损坏值）。
     *
     * 此方法返回当前物品堆叠的耐久值，也称为损坏值，
     * 表示物品在使用过程中所受到的磨损程度。
     *
     * @return 整数值，表示当前物品的耐久（损坏）值。
     */
    fun getDurability() = vanilla.damageValue

    /**
     * 获取物品堆的最大耐久值。
     *
     * 此方法返回当前物品堆的最大耐久值，表示该物品在完全新的状态下可以承受的总损耗次数。
     *
     * @return 当前物品堆的最大耐久值。
     */
    fun getMaxDurability() = vanilla.maxDamage

    /**
     * 获取物品的损坏百分比。
     *
     * 此方法通过将当前物品的损坏值与最大耐久值相除，来计算当前物品的损坏比例。
     * 不可损坏的物品（最大耐久为 0）返回 0.0。
     *
     * @return 表示物品损坏比例的浮点数值，范围 0.0 到 1.0。
     */
    fun getDamagePercent(): Float {
        val maxDamage = vanilla.maxDamage
        return if (maxDamage <= 0) 0f else vanilla.damageValue.toFloat() / maxDamage
    }

    /**
     * 获取当前物品堆栈的稀有度（Rarity）信息，以字符串形式返回。
     *
     * @return 表示当前物品稀有度的字符串。
     */
    fun getRarity(): String = vanilla.getRarity().serializedName

    /**
     * 检查物品是否可以被附魔。
     *
     * @return 如果该物品可以被附魔，返回 `true`；否则返回 `false`。
     */
    fun isEnchantable(): Boolean = vanilla.isEnchantable

    /**
     * 获取物品的附魔列表。
     *
     * 此方法通过访问 `stack` 对象中的 `DataComponentTypes.ENCHANTMENTS` 数据组件，
     * 获取附魔信息，并将其转换为字符串形式的列表返回。
     *
     * 提示上下文取自当前客户端世界（`mc.level`），附魔的本地化名称需要它的注册表；
     * 未进入世界时退化为无注册表上下文，此时返回空列表。
     *
     * @return 包含附魔字符串的列表。如果没有附魔数据，则返回空列表。
     */
    fun getEnchantments() = buildList {
        vanilla.get(DataComponents.ENCHANTMENTS)?.addToTooltip(Item.TooltipContext.of(mc.level), {
            add(it.string)
        }, mc.tooltipFlag, vanilla)
    }

    /**
     * 获取物品堆中存储的附魔名称列表。
     *
     * 该方法从 `stack` 中提取存储的附魔信息，并将其转化为字符串格式存储到一个列表中返回。
     * 如果当前物品堆未包含存储的附魔信息，则返回的列表为空。
     *
     * 提示上下文取自当前客户端世界（`mc.level`），附魔的本地化名称需要它的注册表；
     * 未进入世界时退化为无注册表上下文，此时返回空列表。
     *
     * @return 包含存储附魔名称的列表。
     */
    fun getStoredEnchantments() = buildList {
        vanilla.get(DataComponents.STORED_ENCHANTMENTS)?.addToTooltip(Item.TooltipContext.of(mc.level), {
            add(it.string)
        }, mc.tooltipFlag, vanilla.components)
    }

}