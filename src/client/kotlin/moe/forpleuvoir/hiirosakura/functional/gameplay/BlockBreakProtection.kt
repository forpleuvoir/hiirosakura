package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.block
import moe.forpleuvoir.hiirosakura.config.items.item
import moe.forpleuvoir.hiirosakura.config.items.itemStackBlockInfoMap
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.*
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import kotlin.time.TimeSource

object BlockBreakProtection : ModConfigContainer("block_break_protection") {

    val enabled by keyBindBoolean("enable", false)

    val matcher = itemStackBlockInfoMap(
        "matcher",
        mapOf(
            "Budding Amethyst" to (ItemStackMatcher.anyMatcher to BlockInfoMatcher(
                MultiMatcher.MatchMode.AnyMatch,
                BlockInfoMatchEntry.Block(Blocks.BUDDING_AMETHYST)
            ))
        ),
    )

    @JvmStatic
    fun canBreak(itemStack: ItemStack, blockInfo: BlockInfo): Boolean {
        if (!enabled.value) return true
        return !matcher.any {
            (it.value.block.match(blockInfo) && it.value.item.match(itemStack)).apply {
                if (this && mark.elapsedNow() > Toast.SHORT_DURATION) {
                    mark = TimeSource.Monotonic.markNow()
                    Toast.showToast(HSLang.blockBreakProtection(it.key))
                }
            }
        }
    }

}

private var mark = TimeSource.Monotonic.markNow()