package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean

object RenderInfoAddon : ModConfigContainer("render_info_addon") {

    val showEnchantmentWhenSwitch by keyBindBoolean("show_enchantment_when_switch", value = false)

    val alwaysRenderBarrier by keyBindBoolean("always_render_barrier", value = false)

    val alwaysRenderLight by keyBindBoolean("always_render_light", value = false)

    val disableScoreboardSidebarRender by keyBindBoolean("disable_scoreboard_sidebar_render", value = false)

    init {
        addConfig(DropEntityRenderAddon.Config)
        addConfig(TntRenderConfig)
    }

}