package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcher
import moe.forpleuvoir.hiirosakura.config.items.blockInfoMatcher
import moe.forpleuvoir.hiirosakura.config.items.blockInfoMatcherMap
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.hiirosakura.util.cycle
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.nebula.config.item.impl.stringKeyMap
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import kotlin.time.TimeSource

object BlockBreakProtection {

    object Config : ModConfigContainer("block_break_protection") {

        val enabled by keyBindBoolean("enable", false)

        val matcher = blockInfoMatcherMap(
            "matcher",
            mapOf(
                "Budding Amethyst" to BlockInfoMatcher(
                    MultiMatcher.MatchMode.AnyMatch,
                    BlockInfoMatchEntry.Block(Blocks.BUDDING_AMETHYST)
                )
            ),
        )

    }

    private val matcher by Config.matcher

    private var mark = TimeSource.Monotonic.markNow()

    @JvmStatic
    fun canBreak(block: BlockState, pos: BlockPos): Boolean {
        if (!Config.enabled.value) return true
        val info = BlockInfo(block, pos)
        return !matcher.any {
            it.value.match(info).apply {
                if (this && mark.elapsedNow() > Toast.SHORT_DURATION) {
                    mark = TimeSource.Monotonic.markNow()
                    Toast.showToast(HSLang.blockBreakProtection(it.key))
                }
            }
        }
    }
}