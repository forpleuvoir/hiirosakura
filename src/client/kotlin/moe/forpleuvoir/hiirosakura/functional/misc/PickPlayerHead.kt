package moe.forpleuvoir.hiirosakura.functional.misc

import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.util.PlayerHeadUtil
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items

object PickPlayerHead {

    @JvmStatic
    fun pickPlayerHead(entity: Entity) {
        if (entity !is PlayerEntity || !HSConfig.pickPlayerHeadOnCreative) return
        mc.player?.apply {
            if (this.isCreative) {
                swapSlotWithHotbar {
                    it.item == Items.PLAYER_HEAD
                            && it.components.get(DataComponentTypes.PROFILE)?.gameProfile?.id == entity.uuid
                }.let {
                    if (it == -1) {
                        inventory.insertStack(PlayerHeadUtil.getPlayerHead(entity))
                        swapSlotWithHotbar { item ->
                            item.item == Items.PLAYER_HEAD
                                    && item.components.get(DataComponentTypes.PROFILE)?.gameProfile?.id == entity.uuid
                        }
                    }
                }
            }
        }
    }

}