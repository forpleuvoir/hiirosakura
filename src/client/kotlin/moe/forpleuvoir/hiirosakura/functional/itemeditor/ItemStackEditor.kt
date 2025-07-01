package moe.forpleuvoir.hiirosakura.functional.itemeditor

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.ItemIcon
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

fun ItemStackEditor(
    itemStack: ItemStack = ItemStackMatcher.handItemStack ?: ItemStack(Items.MELON),
) = Dialog {
    TextLabel("ItemStackEditor")
    var itemStack = itemStack.copy()
    Row {
        ItemIcon(itemStack)
    }
}