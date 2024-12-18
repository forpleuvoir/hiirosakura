package moe.forpleuvoir.hiirosakura.functional.misc

import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.util.PlayerHeadUtil
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity

object PickPlayerHead {

    @JvmStatic
    fun pickPlayerHead(entity: Entity) {
        if (!HSConfig.pickPlayerHeadOnCreative) return
        if (entity !is PlayerEntity) return
        mc.player?.apply {
            if (this.isCreative) inventory.insertStack(PlayerHeadUtil.getPlayerHead(entity))
        }
    }

}