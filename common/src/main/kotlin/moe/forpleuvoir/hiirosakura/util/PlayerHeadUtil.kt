package moe.forpleuvoir.hiirosakura.util

import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile

object PlayerHeadUtil {
    private const val OWNER = "SkullOwner"

    @JvmStatic
    fun getPlayerHead(player: Player): ItemStack {
        val stack = ItemStack(Items.PLAYER_HEAD)
        stack.set(DataComponents.PROFILE, ResolvableProfile(player.gameProfile))
        return stack
    }


}