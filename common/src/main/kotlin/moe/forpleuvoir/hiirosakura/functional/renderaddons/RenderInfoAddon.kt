package moe.forpleuvoir.hiirosakura.functional.renderaddons

import moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse.CreeperFuseLayer
import moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse.TntFuseRenderer
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.input.KeyTriggerTiming
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configBoolean
import moe.forpleuvoir.nebula.config.item.configFloat

object RenderInfoAddon : ConfigGroup("render_info_addon") {

    val useIrisCompatiblePipeline by configToggleKeybind(
        "use_iris_compatible_pipeline", false, Keybind(
            defaultSetting = KeybindSetting(
                passthrough = true,
                trigger = KeyTriggerTiming.Press
            )
        )
    )

    val alwaysRenderBarrier by configToggleKeybind("always_render_barrier", false, Keybind())

    val alwaysRenderLight by configToggleKeybind("always_render_light", false, Keybind())

    val disableTextObfuscationRender by configBoolean("disable_text_obfuscation_render", false)

    val disableScoreboardSidebarRender by configToggleKeybind("disable_scoreboard_sidebar_render", false, Keybind())

    init {
        addConfig(GammaOverride)
        addConfig(TntFuseRenderer)
        addConfig(CreeperFuseLayer)
        addConfig(HeldItemRenderAddon)
        addConfig(DropEntityRenderAddon)
    }

}

object GammaOverride : ConfigGroup("gamma_override") {

    val enable by configToggleKeybind("enable", false, Keybind())

    val gamma by configFloat("gamma", 1.0f, 0.0f, 30f)

}