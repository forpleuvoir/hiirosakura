package forpleuvoir.hiirosakura.client.gui

import com.google.common.collect.ImmutableList
import fi.dy.masa.malilib.config.IConfigBase
import fi.dy.masa.malilib.gui.GuiConfigsBase
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.config.HotKeys
import forpleuvoir.hiirosakura.client.config.TogglesHotKeys
import forpleuvoir.hiirosakura.client.gui.GuiConfig.ConfigGuiTab.*
import forpleuvoir.hiirosakura.client.gui.event.EventScreen
import forpleuvoir.hiirosakura.client.gui.task.TaskScreen
import net.minecraft.client.gui.screen.Screen
import java.util.*

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.gui
 *
 * #class_name GuiConfig
 *
 * #create_time 2021/6/15 20:28
 */
class GuiConfig() : GuiConfigsBase(10, 50, HiiroSakuraClient.MOD_ID, null, "hiirosakura.gui.title.configs") {
	constructor(parent: Screen?) : this() {
		this.parent = parent
	}

	override fun initGui() {
		if (tab == EVENT) {
			openGui(EventScreen())
			return
		} else if (tab == TASK) {
			openGui(TaskScreen())
			return
		}
		super.initGui()
		clearOptions()
		var x = 10
		for (tab in values()) {
			x += createButton(x, tab)
		}
	}

	private fun createButton(x: Int, tab: ConfigGuiTab): Int {
		val button = ButtonGeneric(x, 26, -1, 20, tab.displayName)
		button.setEnabled(Companion.tab != tab)
		addButton(button, ButtonListener(tab, this))
		return button.width + 2
	}

	override fun useKeybindSearch(): Boolean {
		return tab == HOTKEYS || tab == TOGGLES_HOTKEYS
	}

	override fun getConfigWidth(): Int {
		return when (tab) {
			TOGGLES -> 80
			VALUES -> 80
			HOTKEYS, TOGGLES_HOTKEYS -> 80
			EVENT, TASK -> super.getConfigWidth()
		}
	}

	override fun getConfigs(): List<ConfigOptionWrapper> {
		val configs: MutableList<out IConfigBase> = when (tab) {
			TOGGLES -> Configs.Toggles.OPTIONS
			TOGGLES_HOTKEYS -> TogglesHotKeys.HOTKEY_LIST
			VALUES -> Configs.Values.OPTIONS
			HOTKEYS -> HotKeys.HOTKEY_LIST
			EVENT, TASK -> ImmutableList.of()
		}
		return ConfigOptionWrapper.createFor(configs)
	}

	private inner class ButtonListener(val tab: ConfigGuiTab, var parent: GuiConfig) : IButtonActionListener {
		override fun actionPerformedWithButton(button: ButtonBase, mouseButton: Int) {
			GuiConfig.tab = this.tab
			when (tab) {
				EVENT -> {
					openGui(EventScreen())
				}
				TASK -> {
					openGui(TaskScreen())
				}
				else -> {
					parent.reCreateListWidget()
					Objects.requireNonNull<WidgetListConfigOptions>(parent.listWidget).resetScrollbarPosition()
					parent.initGui()
				}
			}
		}
	}

	enum class ConfigGuiTab(translationKey: String) {
		TOGGLES("button.config_gui.toggles"),
		TOGGLES_HOTKEYS("button.config_gui.toggles.hotkeys"),
		VALUES("button.config_gui.values"),
		HOTKEYS("button.config_gui.hotkeys"),
		EVENT("button.config_gui.event"),
		TASK("button.config_gui.task");

		private val translationKey: String
		val displayName: String
			get() = StringUtils.translate(translationKey)

		init {
			this.translationKey = String.format("%s.gui.%s", HiiroSakuraClient.MOD_ID, translationKey)
		}
	}

	companion object {
		var tab = TOGGLES
	}
}