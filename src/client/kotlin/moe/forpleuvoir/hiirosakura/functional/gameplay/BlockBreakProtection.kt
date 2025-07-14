package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.matcher.ConfigStringItemStackBlockInfoPairMapWrapper
import moe.forpleuvoir.hiirosakura.config.items.matcher.block
import moe.forpleuvoir.hiirosakura.config.items.matcher.item
import moe.forpleuvoir.hiirosakura.config.items.matcher.itemStackBlockInfoMap
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.*
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.userdata.setGuiWrapper
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
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
    ).setGuiWrapper { config, modifier ->
        ConfigStringItemStackBlockInfoPairMapWrapper(config, modifier, HSLang.name, HSLang.handheldItem, HSLang.targetBlock)
    }

    private var mark = TimeSource.Monotonic.markNow()

    @JvmStatic
    fun canBreak(itemStack: ItemStack, blockInfo: BlockInfo): Boolean {
        if (!enabled.value) return true
        return !matcher.any {
            (it.value.block.match(blockInfo) && it.value.item.match(itemStack)).apply {
                if (this && mark.elapsedNow() > Toast.SHORT_DURATION) {
                    mark = TimeSource.Monotonic.markNow()
                    Toast.showToast(InlineStyleText(HSLang.blockBreakProtection(it.key).plainText))
                }
            }
        }
    }

}

