package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.compose_minecraft.ComposeWarmup
import moe.forpleuvoir.hiirosakura.command.HiirosakuraCommand
import moe.forpleuvoir.hiirosakura.common.HiiroSakuraDataManager
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrappers
import moe.forpleuvoir.hiirosakura.input.InputSimulator
import moe.forpleuvoir.hiirosakura.ui.HiiroSakuraScreenContent
import moe.forpleuvoir.hiirosakura.ui.configwrapper.HSConfigWrapper
import moe.forpleuvoir.hiirosakura.util.DataComponentPreBinder
import moe.forpleuvoir.ibukigourd.config.ClientModConfigHandler
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.event.events.client.ClientTickEvent
import moe.forpleuvoir.nebula.common.api.Initializable

object HiiroSakuraClient {

    private val inits = listOf(
        HiirosakuraCommand,
        HiiroSakuraDataManager,
        InputSimulator,
        CustomRadialMenuManager,
        HSConfigWrapper,
        DataComponentWrappers,
    )

    fun init() {
        inits.forEach(Initializable::init)
        ClientLifecycleEvent.Starting.register {
            ComposeWarmup.warmup { HiiroSakuraScreenContent() }
            // 启动时只有静态注册表也要先绑定：否则 ItemStack 构造就抛
            // "Components not bound yet"，主菜单里的物品界面打不开。
            DataComponentPreBinder.ensureBound()
        }
        // 进世界后注册表才带动态部分（唱片/画/山羊角）：此时重绑一次，把启动时的占位 Holder
        // 换成真值。ensureBound 自带「已用同等注册表绑过就返回」，挂在 tick 上没有额外开销。
        ClientTickEvent.TickStart.register { DataComponentPreBinder.ensureBound() }
        ClientModConfigHandler.register(HSConfig)
    }

}