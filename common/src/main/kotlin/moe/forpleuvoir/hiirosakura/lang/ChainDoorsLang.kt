package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.style

@Suppress("NOTHING_TO_INLINE")
object ChainDoorsLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.chain_doors.$key", args = args)

    inline val originDoor get() = lang("origin_door").style {
        hover(originDoorComment)
    }

    inline val originDoorComment get() = lang("origin_door.comment")

    inline val chainDoor get() = lang("chain_door").style {
        hover(chainDoorComment)
    }

    inline val chainDoorComment get() = lang("chain_door.comment")

    inline val keyToggleMode get() = lang("key_toggle_mode").style {
        hover(keyToggleModeComment)
    }

    inline val keyToggleModeComment get() = lang("key_toggle_mode.comment")

    inline val strategy get() = lang("strategy").style {
        hover(strategyComment)
    }

    inline val strategyComment get() = lang("strategy.comment")

    inline val strategyRadius
        get() = lang("strategy.radius").style { hover(strategyRadiusComment) }

    inline val strategyRadiusComment get() = lang("strategy.radius.comment")

    inline val strategyShape
        get() = lang("strategy.shape").style { hover(strategyShapeComment) }

    inline val strategyShapeComment get() = lang("strategy.shape.comment")

    inline val strategySameBlock
        get() = lang("strategy.same_block").style { hover(strategySameBlockComment) }

    inline val strategySameBlockComment get() = lang("strategy.same_block.comment")

    inline val strategySyncState
        get() = lang("strategy.sync_state").style { hover(strategySyncStateComment) }

    inline val strategySyncStateComment get() = lang("strategy.sync_state.comment")

    inline val strategyLimit
        get() = lang("strategy.limit").style { hover(strategyLimitComment) }

    inline val strategyLimitComment get() = lang("strategy.limit.comment")
}
