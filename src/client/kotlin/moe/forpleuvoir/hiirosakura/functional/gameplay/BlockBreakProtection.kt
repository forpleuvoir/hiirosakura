package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

object BlockBreakProtection {

    val config = ConfigBlockInfoMatcher("block_break_protection", BlockInfoMatcher(MultiMatcher.MatchMode.AnyMatch))

    val blockInfo by config

    @JvmStatic
    fun canBreak(block: BlockState, pos: BlockPos): Boolean {
        val info = BlockInfo(block, pos)
        if (blockInfo.match(info)) {
            Toast.showToast(HSLang.blockBreakProtection)
            return false
        }
        return true
    }
}