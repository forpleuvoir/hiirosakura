package moe.forpleuvoir.hiirosakura.util

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ProfileComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object PlayerHeadUtil {
    private const val OWNER = "SkullOwner"

    @JvmStatic
    fun getPlayerHead(player: PlayerEntity): ItemStack {
        val stack = ItemStack(Items.PLAYER_HEAD)
        stack.set(DataComponentTypes.PROFILE, ProfileComponent(player.gameProfile))
        return stack
    }


}