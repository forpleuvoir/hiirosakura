package forpleuvoir.hiirosakura.client.gui

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.config.HotKeys
import forpleuvoir.hiirosakura.client.config.gui.HiiroSakuraConfigGroup
import forpleuvoir.hiirosakura.client.feature.event.gui.EventScreenTab
import forpleuvoir.hiirosakura.client.feature.timertask.gui.TimerTaskScreenTab
import forpleuvoir.ibuki_gourd.gui.screen.IScreenTabEntry
import forpleuvoir.ibuki_gourd.gui.screen.ScreenBase
import forpleuvoir.ibuki_gourd.gui.screen.ScreenTab
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui

 * 文件名 HiiroSakuraScreen

 * 创建时间 2022/1/18 13:05

 * @author forpleuvoir

 */
object HiiroSakuraScreen {

    val baseTitle: Text = Text.of(HiiroSakuraClient.modName)

    private val toggles = HiiroSakuraConfigGroup("hiirosakura.config.toggles", baseTitle, Configs.Toggles.CONFIGS, 180)
    private val values = HiiroSakuraConfigGroup("hiirosakura.config.values", baseTitle, Configs.Values.CONFIGS)
    private val hotkeys = HiiroSakuraConfigGroup("hiirosakura.config.hotkeys", baseTitle, HotKeys.HOTKEY_LIST)

    private val timerTask: IScreenTabEntry =
        object : HiiroSakuraTabEntry("${HiiroSakuraClient.modId}.screen.timertask") {
            override val screen: ScreenTab
                get() = TimerTaskScreenTab(this)
        }

    private val event: IScreenTabEntry = object : HiiroSakuraTabEntry("${HiiroSakuraClient.modId}.screen.event") {
        override val screen: ScreenTab
            get() = EventScreenTab(this)
    }

    val allTabsEntry: List<IScreenTabEntry> = listOf(
        toggles,
        values,
        hotkeys,
        timerTask,
        event
    )

    var currentEntry: IScreenTabEntry = toggles

    fun current(): ScreenTab {
        return currentEntry.screen
    }

    fun openScreen() {
        ScreenBase.openScreen(current())
    }

    fun openScreen(parent: Screen?) {
        ScreenBase.openScreen(current().apply { this.parent = parent })
    }

}