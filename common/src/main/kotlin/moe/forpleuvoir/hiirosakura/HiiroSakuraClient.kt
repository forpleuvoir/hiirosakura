package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.hiirosakura.command.HiirosakuraCommand
import moe.forpleuvoir.hiirosakura.common.HiiroSakuraDataManager
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrappers
import moe.forpleuvoir.hiirosakura.ui.HiiroSakuraScreenContent
import moe.forpleuvoir.hiirosakura.ui.configwrapper.HSConfigWrapper
import moe.forpleuvoir.hiirosakura.input.InputSimulator
import moe.forpleuvoir.ibukigourd.config.ClientModConfigHandler
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.ui.ComposeSceneWarmup
import moe.forpleuvoir.nebula.common.api.Initializable

object HiiroSakuraClient {

    private val inits = listOf(
        HiirosakuraCommand,
        HiiroSakuraDataManager,
        InputSimulator,
        CustomRadialMenuManager,
        HSConfigWrapper,
    )

    fun init() {
        inits.forEach(Initializable::init)
        ClientLifecycleEvent.Starting.register {
            ComposeSceneWarmup.warmUp { HiiroSakuraScreenContent() }
        }
        ClientModConfigHandler.register(HSConfig)
        DataComponentWrappers.init()
    }

}