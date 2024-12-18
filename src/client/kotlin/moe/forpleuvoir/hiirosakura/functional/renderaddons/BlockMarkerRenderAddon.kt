package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon.alwaysRenderBarrier
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon.alwaysRenderLight
import net.minecraft.block.Block
import net.minecraft.item.Items

object BlockMarkerRenderAddon {

    @JvmStatic
    fun getShowBlockMarker(): Block? {
        return when {
            alwaysRenderBarrier.value -> Block.getBlockFromItem(Items.BARRIER)
            alwaysRenderLight.value   -> Block.getBlockFromItem(Items.LIGHT)
            else                                      -> null
        }
    }


}