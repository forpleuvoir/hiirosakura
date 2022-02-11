package forpleuvoir.hiirosakura.client.gui

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.ibuki_gourd.common.ModInfo
import forpleuvoir.ibuki_gourd.gui.screen.IScreenTabEntry
import net.minecraft.text.Text

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui

 * 文件名 HiiroSakuraTabEntry

 * 创建时间 2022/1/21 16:59

 * @author forpleuvoir

 */
abstract class HiiroSakuraTabEntry(override val key: String) : IScreenTabEntry {
    override val all: List<IScreenTabEntry>
        get() = HiiroSakuraScreen.allTabsEntry
    override val baseTitle: Text
        get() = HiiroSakuraScreen.baseTitle
    override val current: IScreenTabEntry
        get() = HiiroSakuraScreen.currentEntry
    override val remark: String
        get() = key
    override val currentMod: ModInfo
        get() = HiiroSakuraClient

    override fun changeCurrent(current: IScreenTabEntry) {
        HiiroSakuraScreen.currentEntry = current
    }
}