package forpleuvoir.hiirosakura.client.util

import forpleuvoir.ibuki_gourd.common.tText
import forpleuvoir.ibuki_gourd.utils.text
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
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
        EnchantmentHelper.get(stack).forEach { (enchantment, lvl) ->
            val text = enchantment.translationKey.tText()
            if (lvl <= 10 && lvl != 1)
                text.append(TranslatableText("enchantment.level.$lvl"))
            else if (lvl > 10) text.append("$lvl".text)
            text.formatted(*formatting)
            texts.add(text)
        }
        return texts
    }
}