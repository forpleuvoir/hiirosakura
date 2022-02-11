package forpleuvoir.hiirosakura.client.compat.ibuki_gourd

import forpleuvoir.hiirosakura.client.gui.HiiroSakuraScreen
import forpleuvoir.ibuki_gourd.api.screen.IbukiGourdScreenApi
import forpleuvoir.ibuki_gourd.gui.screen.ScreenTab
import net.minecraft.client.gui.screen.Screen

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.compat.ibuki_gourd

 * 文件名 ScreenImpl

 * 创建时间 2022/2/11 23:23

 * @author forpleuvoir

 */
class ScreenImpl : IbukiGourdScreenApi {
    override fun getScreenTabFactory(): (Screen?) -> ScreenTab {
        return {
            HiiroSakuraScreen.current().apply { parent = it }
        }
    }
}