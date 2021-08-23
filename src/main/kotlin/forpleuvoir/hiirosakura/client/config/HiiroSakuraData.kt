package forpleuvoir.hiirosakura.client.config

import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject
import fi.dy.masa.malilib.config.IConfigHandler
import fi.dy.masa.malilib.util.FileUtils
import fi.dy.masa.malilib.util.JsonUtils
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData.Companion.readData
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData.Companion.writeData
import forpleuvoir.hiirosakura.client.feature.event.base.HiiroSakuraEvents
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessageSend
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessageSort
import forpleuvoir.hiirosakura.client.feature.regex.ServerChatMessageRegex
import forpleuvoir.hiirosakura.client.feature.task.HiiroSakuraTimeTask
import forpleuvoir.hiirosakura.client.feature.tooltip.Tooltip
import java.io.File

/**
 * 数据类
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.config
 *
 * #class_name HiiroSakuraDatas
 *
 * #create_time 2021/6/16 22:18
 */
class HiiroSakuraData : IConfigHandler {
	override fun load() {
		val configFile = File(CONFIG_FILE_PATH, CONFIG_FILE_NAME)
		if (configFile.isFile && configFile.canRead() && configFile.exists()) {
			val element = JsonUtils.parseJsonFile(configFile)
			if (element != null && element.isJsonObject) {
				val root = element.asJsonObject
				readData(root, "DATAS", DATA)
			}
		}
	}

	override fun save() {
		val dir = CONFIG_FILE_PATH
		if (dir.exists() && dir.isDirectory || dir.mkdirs()) {
			val root = JsonObject()
			writeData(root, "DATAS", DATA)
			JsonUtils.writeJsonToFile(root, File(dir, CONFIG_FILE_NAME))
		}
	}

	companion object {
		val configHandler: IConfigHandler = HiiroSakuraData()
		val CONFIG_FILE_PATH = File(FileUtils.getConfigDirectory(), HiiroSakuraClient.MOD_ID)
		private const val CONFIG_FILE_NAME = HiiroSakuraClient.MOD_ID + "_data.json"

		@JvmField
		val QUICK_CHAT_MESSAGE_SEND = QuickChatMessageSend()

		@JvmField
		val TOOLTIP = Tooltip()

		@JvmField
		val SERVER_CHAT_MESSAGE_REGEX = ServerChatMessageRegex()

		@JvmField
		val QUICK_CHAT_MESSAGE_SORT = QuickChatMessageSort()

		@JvmField
		val HIIRO_SAKURA_EVENTS = HiiroSakuraEvents()

		@JvmField
		val HIIRO_SAKURA_TIME_TASK = HiiroSakuraTimeTask()

		val DATA: List<AbstractHiiroSakuraData> = ImmutableList.of(
			QUICK_CHAT_MESSAGE_SEND,
			TOOLTIP,
			SERVER_CHAT_MESSAGE_REGEX,
			QUICK_CHAT_MESSAGE_SORT,
			HIIRO_SAKURA_EVENTS,
			HIIRO_SAKURA_TIME_TASK
		)

		fun initialize() {
			configHandler.load()
		}
	}
}