package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType

fun ClientPlayerEntity.swapSlotWithHotbar(predicate: (ItemStack) -> Boolean): Int {
    val interactionManager = mc.interactionManager!!
    val inv = this.inventory
    inv.main
        .find(predicate)
        ?.let { item ->
            val index = inv.main.indexOf(item)
            if (index >= 9) {
                interactionManager.clickSlot(this.playerScreenHandler.syncId, index, inv.selectedSlot, SlotActionType.SWAP, this)
                return index
            } else {
                val old = inv.selectedSlot
                inv.setSelectedSlot(index)
                return old
            }
        }
    return -1
}

fun ClientPlayerEntity.swapSlotWithHotbar(index: Int) {
    if (index < 0) return
    val inv = this.inventory
    val interactionManager = mc.interactionManager!!
    if (index >= 9)
        interactionManager.clickSlot(this.playerScreenHandler.syncId, index, inv.selectedSlot, SlotActionType.SWAP, this)
    else inv.setSelectedSlot(index)
}
