@file:Suppress("unused")

package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.hiirosakura.util.tooltipFlag
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.*

/**
 * HSItemStack 类用于封装 Minecraft 中的物品堆栈(ItemStack)对象，并提供相关的属性和辅助方法以获取堆栈信息。
 *
 * @property stack 被封装的物品堆栈对象。
 */
class HSItemStack(internal val stack: ItemStack) {

    companion object {
        @JvmStatic
        fun fromItemStack(stack: ItemStack) = HSItemStack(stack)
    }

    /**
     * 获取堆栈中的物品数量。
     *
     * @return 一个整数，表示堆栈中物品的数量。
     */
    fun getCount(): Int = stack.count

    fun getMaxCount(): Int = stack.maxStackSize

    fun isStackable() = stack.isStackable

    fun hasComponent(componentType: String): Boolean = stack.has(BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse(componentType)).get().value())

    /**
     * 获取当前物品的唯一标识符，并将其转换为字符串形式。
     *
     * 该方法通过 `Registries.ITEM` 注册表，获取当前物品的唯一标识符，
     * 然后返回其字符串表示形式，用于唯一标记和识别物品。
     *
     * @return 当前物品唯一标识符的字符串形式。
     */
    fun getItem() = BuiltInRegistries.ITEM.getId(stack.item).toString()

    fun getTags(): List<String> = stack.tags.map { it.location.toString() }.toList()

    fun hasTag(tag: String) = stack.tags.anyMatch { it.location.toString() == tag }

    /**
     * 获取物品的名称。
     *
     * @return 表示物品名称的字符串。
     */
    fun getName(): String = stack.hoverName.string

    /**
     * 获取物品名称的字符串表示。
     *
     * @return 当前物品名称的字符串形式。
     */
    fun getItemName(): String = stack.itemName.string


    fun isShovel() = stack.item is ShovelItem

    fun isHoe() = stack.item is HoeItem

    fun isAxe() = stack.item is AxeItem

    fun isShield() = stack.item is ShieldItem

    fun isBlock() = stack.item is BlockItem

    fun isDamageable() = stack.isDamageableItem

    /**
     * 获取物品耐久值（损坏值）。
     *
     * 此方法返回当前物品堆叠的耐久值，也称为损坏值，
     * 表示物品在使用过程中所受到的磨损程度。
     *
     * @return 整数值，表示当前物品的耐久（损坏）值。
     */
    fun getDurability() = stack.damageValue

    /**
     * 获取物品堆的最大耐久值。
     *
     * 此方法返回当前物品堆的最大耐久值，表示该物品在完全新的状态下可以承受的总损耗次数。
     *
     * @return 当前物品堆的最大耐久值。
     */
    fun getMaxDurability() = stack.maxDamage

    /**
     * 获取物品的损坏百分比。
     *
     * 此方法通过将当前物品的损坏值与最大耐久值相除，来计算当前物品的损坏比例。
     * 返回的百分比值以浮点数表示，范围为 0.0 到 1.0。
     *
     * @return 表示物品损坏比例的浮点数值。
     */
    fun getDamagePercent() = stack.damageValue.toFloat() / stack.maxDamage

    /**
     * 获取当前物品堆栈的稀有度（Rarity）信息，以字符串形式返回。
     *
     * @return 表示当前物品稀有度的字符串。
     */
    fun getRarity(): String = stack.getRarity().serializedName

    /**
     * 检查物品是否可以被附魔。
     *
     * @return 如果该物品可以被附魔，返回 `true`；否则返回 `false`。
     */
    fun isEnchantable(): Boolean = stack.isEnchantable

    /**
     * 获取物品的附魔列表。
     *
     * 此方法通过访问 `stack` 对象中的 `DataComponentTypes.ENCHANTMENTS` 数据组件，
     * 获取附魔信息，并将其转换为字符串形式的列表返回。
     *
     * 通过 `appendTooltip` 方法遍历附魔数据，并将每个附魔的字符串表示形式添加到结果列表中。
     *
     * @return 包含附魔字符串的列表。如果没有附魔数据，则返回空列表。
     */
    fun getEnchantments() = buildList<String> {
        stack.get(DataComponents.ENCHANTMENTS)?.addToTooltip(Item.TooltipContext.EMPTY, {
            add(it.string)
        }, mc.tooltipFlag, stack)
    }

    /**
     * 获取物品堆中存储的附魔名称列表。
     *
     * 该方法从 `stack` 中提取存储的附魔信息，并将其转化为字符串格式存储到一个列表中返回。
     * 如果当前物品堆未包含存储的附魔信息，则返回的列表为空。
     *
     * @return 包含存储附魔名称的列表。
     */
    fun getStoredEnchantments() = buildList<String> {
        stack.get(DataComponents.STORED_ENCHANTMENTS)?.addToTooltip(Item.TooltipContext.EMPTY, {
            add(it.string)
        }, mc.tooltipFlag, stack)
    }

}