package forpleuvoir.hiirosakura.client.config

import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData.Companion.readData
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData.Companion.writeData
import forpleuvoir.hiirosakura.client.feature.event.gui.HiiroSakuraEvent
import forpleuvoir.hiirosakura.client.feature.timertask.gui.HiiroSakuraTimerTask
import forpleuvoir.hiirosakura.client.feature.tooltip.Tooltip
import forpleuvoir.ibuki_gourd.common.IModInitialize
import forpleuvoir.ibuki_gourd.config.ConfigUtil
import forpleuvoir.ibuki_gourd.config.IConfigHandler
import forpleuvoir.ibuki_gourd.config.options.ConfigBase
import java.io.File

/**
 * 数据类
 *
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.config
 *
 * 文件名 HiiroSakuraDatas
 *
 * 创建时间 2021/6/16 22:18
 *
 *  @author forpleuvoir
 */
object HiiroSakuraData : IConfigHandler, IModInitialize {

	override fun load() {
		val configFile = File(CONFIG_FILE_PATH.toFile(), CONFIG_FILE_NAME)
		if (configFile.isFile && configFile.canRead() && configFile.exists()) {
			val element = ConfigUtil.paresJsonFile(configFile)
			if (element != null && element.isJsonObject) {
				val root = element.asJsonObject
				readData(root, DATA)
			}
		}
	}

	override fun save() {
		val dir = CONFIG_FILE_PATH.toFile()
		if (dir.exists() && dir.isDirectory || dir.mkdirs()) {
			val root = JsonObject()
			writeData(root, DATA)
			ConfigUtil.writeJsonToFile(root, File(CONFIG_FILE_PATH.toFile(), CONFIG_FILE_NAME))
		}
	}

	override fun allConfig(): List<ConfigBase> {
		return emptyList()
	}

	private val CONFIG_FILE_PATH = ConfigUtil.configFileDir(HiiroSakuraClient)
	private val CONFIG_FILE_NAME get() = HiiroSakuraClient.modId + "_data.json"


	@JvmField
	val TOOLTIP = Tooltip()

	@JvmField
	val TIMER_TASK = HiiroSakuraTimerTask()

	@JvmField
	val EVENT = HiiroSakuraEvent()

	private val DATA: List<AbstractHiiroSakuraData> = ImmutableList.of(
		TOOLTIP, TIMER_TASK, EVENT
	)

	override fun initialize() {
		DATA.forEach { it.initialize() }
	}

}