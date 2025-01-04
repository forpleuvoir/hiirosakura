package moe.forpleuvoir.hiirosakura.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.customdata.CustomData
import moe.forpleuvoir.hiirosakura.functional.event.HSEventManager
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.util.loader
import moe.forpleuvoir.nebula.common.util.ioLaunch
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import moe.forpleuvoir.nebula.serialization.gson.jsonStringToObject
import moe.forpleuvoir.nebula.serialization.gson.toJsonString
import java.io.File

@EventSubscriber
object HiiroSakuraDataManager {

    private val log = logger()

    private val datas = listOf(
        TaskManager,
        HSEventManager,
        CustomData
    )

    private val dataPath = File(loader.configDir.toFile(), "${HiiroSakura.MOD_ID}/data").toPath()


    @Subscriber
    fun init(event: ClientLifecycleEvent.ClientStartingEvent) {
        runBlocking {
            log.info("Loading data...")
            datas.forEach { data ->
                log.info("Loading data: ${data.key}")
                loadData(data)
            }
        }
    }

    @Subscriber
    fun onSave(event: ClientLifecycleEvent.ClientStopEvent) {
        runBlocking {
            datas.forEach {
                saveData(it)
            }
        }
    }

    fun asyncLoad() {
        datas.forEach {
            ioLaunch { loadData(it) }
        }
    }

    private suspend fun loadData(data: HiiroSakuraData) = withContext(Dispatchers.IO) {
        runCatching {
            ConfigUtil.run {
                //todo 换成Reader
                val file = configFile("${data.key}.json", dataPath)
                val json = readFileToString(file)
                data.deserialization(json.jsonStringToObject())
            }
        }.onFailure {
            saveData(data)
            log.warn(it)
        }
    }

    fun asyncSave() {
        datas.forEach {
            ioLaunch { saveData(it) }
        }
    }

    private suspend fun saveData(data: HiiroSakuraData) = withContext(Dispatchers.IO) {
        runCatching {
            ConfigUtil.run {
                //todo 换成Reader
                val file = configFile("${data.key}.json", dataPath)
                val str = (data.serialization() as SerializeObject).toJsonString()
                writeToFile(str, file)
            }
        }.onFailure {
            log.warn(it)
        }
    }

}

