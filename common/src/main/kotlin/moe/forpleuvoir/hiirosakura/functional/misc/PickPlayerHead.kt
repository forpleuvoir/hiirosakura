package moe.forpleuvoir.hiirosakura.functional.misc

import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.util.PlayerHeadUtil
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items

object PickPlayerHead {

    @JvmStatic
    fun pickPlayerHead(entity: Entity) {
        if (entity !is Player || !HSConfig.pickPlayerHeadOnCreative) return
        mc.player?.apply {
            if (this.isCreative) {
                swapSlotWithHotbar {
                    it.item == Items.PLAYER_HEAD
                            && it.components.get(DataComponents.PROFILE)?.partialProfile()?.id == entity.uuid
                }.let {
                    if (it == -1) {
                        inventory.add(PlayerHeadUtil.getPlayerHead(entity))
                        swapSlotWithHotbar { item ->
                            item.item == Items.PLAYER_HEAD
                                    && item.components.get(DataComponents.PROFILE)?.partialProfile()?.id == entity.uuid
                        }
                    }
                }
            }
        }
    }

}