package forpleuvoir.hiirosakura.client.config

import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject
import fi.dy.masa.malilib.config.ConfigUtils
import fi.dy.masa.malilib.config.IConfigBase
import fi.dy.masa.malilib.config.IConfigHandler
import fi.dy.masa.malilib.config.options.*
import fi.dy.masa.malilib.util.FileUtils
import fi.dy.masa.malilib.util.JsonUtils
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import java.io.File

/**
 * Mod配置
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.config
 *
 * #class_name Configs
 *
 * #create_time 2021/6/15 20:04
 */
class Configs : IConfigHandler {
	/**
	 * 各种开关
	 */
	object Toggles {
		@JvmField
		val CHAT_SHOW = ConfigBoolean(
			translationKey("chatShow"), false,
			translationKey("chatShow.comment")
		)

		@JvmField
		val SHOW_ENCHANTMENT = ConfigBoolean(
			translationKey("showEnchantment"), false,
			translationKey("showEnchantment.comment")
		)

		@JvmField
		val SHOW_TNT_FUSE = ConfigBoolean(
			translationKey("showTNTFuse"), false,
			translationKey("showTNTFuse.comment")
		)

		@JvmField
		val SHOW_ITEM_ENTITY_NAME = ConfigBoolean(
			translationKey("showItemEntityName"), false,
			translationKey("showItemEntityName.comment")
		)

		@JvmField
		val SHOW_ITEM_ENTITY_ENCHANTMENT = ConfigBoolean(
			translationKey("showItemEntityEnchantment"), false,
			translationKey("showItemEntityEnchantment.comment")
		)

		@JvmField
		val SHOW_ITEM_ENTITY_COUNT = ConfigBoolean(
			translationKey("showItemEntityCount"), false,
			translationKey("showItemEntityCount.comment")
		)

		@JvmField
		val SHOW_ENTITY_AGE = ConfigBoolean(
			translationKey("showEntityAge"), false,
			translationKey("showEntityAge.comment")
		)

		@JvmField
		val AUTO_REBIRTH = ConfigBoolean(
			translationKey("autoRebirth"), false,
			translationKey("autoRebirth.comment")
		)

		@JvmField
		val SHOW_TOOLTIP = ConfigBoolean(
			translationKey("showTooltip"), false,
			translationKey("showTooltip.comment")
		)

		@JvmField
		val SHOW_TOOLTIP_ON_ITEM_TOGGLE = ConfigBoolean(
			translationKey("showTooltipOnItemToggle"), false,
			translationKey("showTooltipOnItemToggle.comment")
		)

		@JvmField
		val SHOW_TOOLTIP_ON_ITEM_ENTITY = ConfigBoolean(
			translationKey("showTooltipOnItemEntity"), false,
			translationKey("showTooltipOnItemEntity.comment")
		)

		@JvmField
		val ENABLE_QCMS_GUI = ConfigBoolean(
			translationKey("enableQcmsGui"), false,
			translationKey("enableQcmsGui.comment")
		)

		@JvmField
		val CHAT_MESSAGE_INJECT = ConfigBoolean(
			translationKey("chatMessageInject"), false,
			translationKey("chatMessageInject.comment")
		)

		@JvmField
		val ENABLE_CHAT_MESSAGE_INJECT_REGEX = ConfigBoolean(
			translationKey("enableChatMessageInjectRegex"), false,
			translationKey("enableChatMessageInjectRegex.comment")
		)

		@JvmField
		val REVERSE_CHAT_MESSAGE_INJECT_REGEX = ConfigBoolean(
			translationKey("reverseChatMessageInjectRegex"), false,
			translationKey("reverseChatMessageInjectRegex.comment")
		)

		@JvmField
		val DISABLE_SCOREBOARD_SIDEBAR_RENDER = ConfigBoolean(
			translationKey("disableScoreboardSidebarRender"), false,
			translationKey("disableScoreboardSidebarRender.comment")
		)

		@JvmField
		val CHAT_MESSAGE_FILTER = ConfigBoolean(
			translationKey("chatMessageFilter"), false,
			translationKey("chatMessageFilter.comment")
		)

		@JvmField
		val DISABLE_BLOCK_INTERACTION = ConfigBoolean(
			translationKey("disableBlockInteraction"), false,
			translationKey("disableBlockInteraction.comment")
		)

		@JvmField
		val EXPERIENCE_ORB_ENTITY_VALUE_RENDER = ConfigBoolean(
			translationKey("experienceOrbEntityValueRender"), false,
			translationKey("experienceOrbEntityValueRender.comment")
		)

		@JvmField
		val ENABLE_PLAYER_TICK_EVENT = ConfigBoolean(
			translationKey("enablePlayerTickEvent"), false,
			translationKey("enablePlayerTickEvent.comment")
		)

		@JvmField
		val OPTIONS: ImmutableList<IConfigBase> = ImmutableList.of(
			CHAT_SHOW, SHOW_ENCHANTMENT, SHOW_TNT_FUSE, SHOW_ITEM_ENTITY_NAME,
			SHOW_ITEM_ENTITY_ENCHANTMENT, SHOW_ITEM_ENTITY_COUNT, SHOW_ENTITY_AGE, AUTO_REBIRTH, SHOW_TOOLTIP,
			SHOW_TOOLTIP_ON_ITEM_TOGGLE, SHOW_TOOLTIP_ON_ITEM_ENTITY, ENABLE_QCMS_GUI, CHAT_MESSAGE_INJECT,
			ENABLE_CHAT_MESSAGE_INJECT_REGEX, REVERSE_CHAT_MESSAGE_INJECT_REGEX, DISABLE_SCOREBOARD_SIDEBAR_RENDER,
			CHAT_MESSAGE_FILTER, DISABLE_BLOCK_INTERACTION, EXPERIENCE_ORB_ENTITY_VALUE_RENDER,
			ENABLE_PLAYER_TICK_EVENT
		)

		private fun translationKey(key: String?): String {
			return translationKey("toggles", key)
		}
	}

	object Values {
		@JvmField
		val ITEM_ENTITY_TEXT_RENDER_DISTANCE = ConfigDouble(
			translationKey("itemEntityTextRenderDistance"), 50.0, 0.0, 999.0,
			translationKey("itemEntityTextRenderDistance.comment")
		)

		@JvmField
		val CHAT_SHOW_HEIGHT = ConfigDouble(
			translationKey("chatShowHeight"), 1.0, 0.0, 10.0,
			translationKey("chatShowHeight.comment")
		)

		@JvmField
		val CHAT_SHOW_TEXT_COLOR = ConfigColor(
			translationKey("chatShowTextColor"), "#AFFFFFFF",
			translationKey("chatShowTextColor.comment")
		)

		@JvmField
		val CHAT_SHOW_TEXT_MAX_WIDTH = ConfigInteger(
			translationKey("chatShowTextMaxWidth"), 96, 9, 480,
			translationKey("chatShowTextMaxWidth.comment")
		)

		@JvmField
		val CHAT_SHOW_TIME = ConfigInteger(
			translationKey("chatShowTime"), 200, 0, 9999,
			translationKey("chatShowTime.comment")
		)

		@JvmField
		val CHAT_SHOW_SCALE = ConfigDouble(
			translationKey("chatShowScale"), 1.0, 0.1, 10.0,
			translationKey("chatShowScale.comment")
		)

		@JvmField
		val CHAT_MESSAGE_DEFAULT_REGEX = ConfigString(
			translationKey("chatMessageDefaultRegex"), "(<(?<name>(.*))>)\\s(?<message>.*)",
			translationKey("chatMessageDefaultRegex.comment")
		)

		@JvmField
		val CHAT_MESSAGE_INJECT_PREFIX = ConfigString(
			translationKey("chatMessageInjectPrefix"), "",
			translationKey("chatMessageInjectPrefix.comment")
		)

		@JvmField
		val CHAT_MESSAGE_INJECT_SUFFIX = ConfigString(
			translationKey("chatMessageInjectSuffix"), "",
			translationKey("chatMessageInjectSuffix.comment")
		)

		@JvmField
		val CHAT_MESSAGE_INJECT_REGEX = ConfigStringList(
			translationKey("chatMessageInjectRegex"), ImmutableList.of("^\\/"),
			translationKey("chatMessageInjectRegex.comment")
		)

		@JvmField
		val CHAT_MESSAGE_FILTER_REGEX = ConfigStringList(
			translationKey("chatMessageFilterRegex"), ImmutableList.of(""),
			translationKey("chatMessageFilterRegex.comment")
		)

		@JvmField
		val OPTIONS: ImmutableList<IConfigBase> = ImmutableList.of(
			ITEM_ENTITY_TEXT_RENDER_DISTANCE, CHAT_SHOW_HEIGHT, CHAT_SHOW_TEXT_COLOR, CHAT_SHOW_TEXT_MAX_WIDTH,
			CHAT_SHOW_TIME, CHAT_SHOW_SCALE, CHAT_MESSAGE_DEFAULT_REGEX, CHAT_MESSAGE_INJECT_PREFIX,
			CHAT_MESSAGE_INJECT_SUFFIX, CHAT_MESSAGE_INJECT_REGEX, CHAT_MESSAGE_FILTER_REGEX
		)

		private fun translationKey(key: String?): String {
			return translationKey("values", key)
		}
	}

	override fun load() {
		val configFile = File(CONFIG_FILE_PATH, CONFIG_FILE_NAME)
		if (configFile.isFile && configFile.canRead() && configFile.exists()) {
			val element = JsonUtils.parseJsonFile(configFile)
			if (element != null && element.isJsonObject) {
				val root = element.asJsonObject
				ConfigUtils.readConfigBase(root, "Toggles", Toggles.OPTIONS)
				ConfigUtils.readConfigBase(root, "Values", Values.OPTIONS)
				ConfigUtils.readConfigBase(root, "Hotkeys", HotKeys.HOTKEY_LIST)
				ConfigUtils.readConfigBase(root, "TogglesHotKeys", TogglesHotKeys.HOTKEY_LIST)
			}
		}
	}

	override fun save() {
		val dir = CONFIG_FILE_PATH
		if (dir.exists() && dir.isDirectory || dir.mkdirs()) {
			val root = JsonObject()
			ConfigUtils.writeConfigBase(root, "Toggles", Toggles.OPTIONS)
			ConfigUtils.writeConfigBase(root, "Values", Values.OPTIONS)
			ConfigUtils.writeConfigBase(root, "Hotkeys", HotKeys.HOTKEY_LIST)
			ConfigUtils.writeConfigBase(root, "TogglesHotKeys", TogglesHotKeys.HOTKEY_LIST)
			JsonUtils.writeJsonToFile(root, File(dir, CONFIG_FILE_NAME))
		}
	}

	companion object {
		val configHandler: IConfigHandler = Configs()

		@JvmField
		val CONFIG_FILE_PATH = File(FileUtils.getConfigDirectory(), HiiroSakuraClient.MOD_ID)
		private const val CONFIG_FILE_NAME = HiiroSakuraClient.MOD_ID + "_config.json"

		@JvmStatic
		fun translationKey(type: String?, key: String?): String {
			return String.format("%s.config.%s.%s", HiiroSakuraClient.MOD_ID, type, key)
		}
	}
}