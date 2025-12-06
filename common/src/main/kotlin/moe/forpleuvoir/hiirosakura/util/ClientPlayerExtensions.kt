package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.ItemStack

fun LocalPlayer.swapSlotWithHotbar(predicate: (ItemStack) -> Boolean): Int {
    val interactionManager = mc.gameMode!!
    val inv = this.inventory
    inv.nonEquipmentItems
        .find(predicate)
        ?.let { item ->
            val index = inv.nonEquipmentItems.indexOf(item)
            if (index >= 9) {
                interactionManager.handleInventoryMouseClick(this.inventoryMenu.containerId, index, inv.selectedSlot, ClickType.SWAP, this)
                return index
            } else {
                val old = inv.selectedSlot
                inv.selectedSlot = index
                return old
            }
        }
    return -1
}

fun LocalPlayer.swapSlotWithHotbar(matcher: ItemStackMatcher): Int =
    swapSlotWithHotbar { matcher.match(it) }

fun LocalPlayer.swapSlotWithHotbar(index: Int) {
    if (index < 0) return
    val inv = this.inventory
    val interactionManager = mc.gameMode!!
    if (index >= 9)
        interactionManager.handleInventoryMouseClick(this.inventoryMenu.containerId, index, inv.selectedSlot, ClickType.SWAP, this)
    else inv.selectedSlot = index
}
