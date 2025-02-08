package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcher
import moe.forpleuvoir.hiirosakura.config.items.blockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
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
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

object BlockBreakProtection {

    object Config : ModConfigContainer("block_break_protection") {

        val enabled by keyBindBoolean("enable", false)

        val switchMode by keyBind("switch_match_mode", KeyBind {
            matcher.getValue().mode = matcher.getValue().mode.cycle()
            matcher.onChange(matcher)
            Toast.showToast(
                text =
                    Config.translateText
                        .appendLiteral("->")
                        .append(configs().find { c -> c.key == "switch_match_mode" }!!.translateText).append(Literal(" : "))
                        .append(
                            matcher.getValue().mode.translateText
                        )
            )
        })

        val matcher = blockInfoMatcher("matcher", BlockInfoMatcher(MultiMatcher.MatchMode.AnyMatch))

    }

    private val matcher by Config.matcher

    @JvmStatic
    fun canBreak(block: BlockState, pos: BlockPos): Boolean {
        if (!Config.enabled.value) return true
        val info = BlockInfo(block, pos)
        if (matcher.match(info)) {
            Toast.showToast(HSLang.blockBreakProtection)
            return false
        }
        return true
    }
}