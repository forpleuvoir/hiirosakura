package forpleuvoir.hiirosakura.client.util

import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import java.util.*

/**
 * 物品栈工具
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.util
 *
 * 文件名 ItemStackUtil
 *
 * 创建时间 2021/6/13 16:12
 */
object ItemStackUtil {
    /**
     * 获取该物品栈的所有附魔属性以及等级文本 格式如  (抢夺 III)
     *
     * 等级为1的附魔则不会显示等级
     * @param stack [ItemStack]
     * @param formatting [Formatting]
     * @return 附魔文本列表
     */
    @JvmStatic
    fun getEnchantmentsWithLvl(stack: ItemStack, vararg formatting: Formatting?): List<Text> {
        val texts = LinkedList<Text>()
        var enchantments: NbtList = stack.enchantments
        if (stack.isOf(Items.ENCHANTED_BOOK)) {
            enchantments = EnchantedBookItem.getEnchantmentNbt(stack)
        }
        if (enchantments.isEmpty()) {
            return texts
        }
        enchantments.forEach { nbtElement: NbtElement? ->
            if (nbtElement is NbtCompound) {
                val lvl: Int = nbtElement.getInt("lvl")
                val originKey: Array<String> = nbtElement.getString("id").split(":").toTypedArray()
                val key = String.format("enchantment.%s.%s", originKey[0], originKey[1])
                val text = TranslatableText(key).append(" ")
                if (lvl != 1) {
                    text.append(TranslatableText("enchantment.level.$lvl"))
                }
                text.formatted(*formatting)
                texts.addLast(text)
            }
        }
        return texts
    }
}