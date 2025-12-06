package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon.alwaysRenderBarrier
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon.alwaysRenderLight
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block

object BlockMarkerRenderAddon {

    @JvmStatic
    fun getShowBlockMarker(): Block? {
        return when {
            alwaysRenderBarrier.value -> Block.byItem(Items.BARRIER)
            alwaysRenderLight.value   -> Block.byItem(Items.LIGHT)
            else                                      -> null
        }
    }

}