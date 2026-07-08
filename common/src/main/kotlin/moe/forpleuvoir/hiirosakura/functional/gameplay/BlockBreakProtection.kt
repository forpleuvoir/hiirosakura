package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.matcher.BlockInfoItemStackPairMapWrapper
import moe.forpleuvoir.hiirosakura.config.items.matcher.block
import moe.forpleuvoir.hiirosakura.config.items.matcher.configBlockInfoItemStackMap
import moe.forpleuvoir.hiirosakura.config.items.matcher.item
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.*
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.uiWrapper
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.ui.toast.ToastStrategy
import moe.forpleuvoir.nebula.config.ConfigGroup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks

object BlockBreakProtection : ConfigGroup("block_break_protection") {

    val enabled by configToggleKeybind("enable", false)

    val matcher = configBlockInfoItemStackMap(
        "matcher",
        mapOf(
            "Budding Amethyst" to (BlockInfoMatcher(
                CompositeMatcher.MatchMode.AnyMatch,
                BlockInfoMatchEntry.Block(Blocks.BUDDING_AMETHYST)
            ) to ItemStackMatcher.anyMatcher)
        ),
    ).uiWrapper { config ->
        BlockInfoItemStackPairMapWrapper(
            config,
            reverseValueColumnOrder = true,
            keyHeader = {
                Text(HSLang.Common.name)
            }
        )
    }

    @JvmStatic
    fun canBreak(itemStack: ItemStack, blockInfo: BlockInfo): Boolean {
        if (!enabled.enabled) return true
        return !matcher.any {
            (it.value.block.match(blockInfo) && it.value.item.match(itemStack)).apply {
                if (this) {
                    ToastHandler.showContent(strategy = ToastStrategy.Tagged.Refresh("hs:block_break_protection:${it.key}")) {
                        Text(
                            InlineStyleText(
                                HSLang.Gameplay.blockBreakProtection(
                                    it.key
                                ).plainText
                            )
                        )
                    }
                }
            }
        }
    }

}

