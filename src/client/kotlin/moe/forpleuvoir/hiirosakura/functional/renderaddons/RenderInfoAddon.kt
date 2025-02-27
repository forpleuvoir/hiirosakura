package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse.CreeperFeatureRenderer
import moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse.TntFuseRenderer
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.float

object RenderInfoAddon : ModConfigContainer("render_info_addon") {

    val alwaysRenderBarrier by keyBindBoolean("always_render_barrier", value = false)

    val alwaysRenderLight by keyBindBoolean("always_render_light", value = false)

    val disableTextObfuscationRender by boolean("disable_text_obfuscation_render", false)

    val disableScoreboardSidebarRender by keyBindBoolean("disable_scoreboard_sidebar_render", value = false)

    init {
        addConfig(GammaOverride)
        addConfig(TntFuseRenderer)
        addConfig(ShowEnchantmentWhenSwitch)
        addConfig(CreeperFeatureRenderer)
        addConfig(DropEntityRenderAddon)
    }

}



object GammaOverride : ModConfigContainer("gamma_override") {

    val enable by keyBindBoolean("enable", value = false)

    val gamma by float("gamma", 1.0f, 0.0f, 30f)

}