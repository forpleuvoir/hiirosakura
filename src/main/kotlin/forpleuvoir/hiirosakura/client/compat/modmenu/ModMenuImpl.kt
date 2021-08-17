package forpleuvoir.hiirosakura.client.compat.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import forpleuvoir.hiirosakura.client.gui.GuiConfig
import net.minecraft.client.gui.screen.Screen

/**
 * ModMenu 接口
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.compat.modmenu
 *
 * #class_name ModMenuImpl
 *
 * #create_time 2021/6/10 21:50
 */
class ModMenuImpl : ModMenuApi {
	override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
		return ConfigScreenFactory<Screen> { screen: Screen? ->
			GuiConfig(screen)
		}
	}
}