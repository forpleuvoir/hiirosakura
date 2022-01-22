package forpleuvoir.hiirosakura.client.compat.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import forpleuvoir.hiirosakura.client.gui.HiiroSakuraScreen

/**
 * ModMenu 接口
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.compat.modmenu
 *
 * 文件名 ModMenuImpl
 *
 * 创建时间 2021/6/10 21:50
 */
class ModMenuImpl : ModMenuApi {
	override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
		return ConfigScreenFactory {
			return@ConfigScreenFactory HiiroSakuraScreen.current().apply { parent = it }
		}
	}
}