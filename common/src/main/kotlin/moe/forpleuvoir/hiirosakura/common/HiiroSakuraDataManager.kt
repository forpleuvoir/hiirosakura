package moe.forpleuvoir.hiirosakura.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.event.HSEventManager
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.nebula.common.api.Initializable
import moe.forpleuvoir.nebula.common.util.ioLaunch
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import moe.forpleuvoir.nebula.serialization.json.JsonDialect
import java.io.File

object HiiroSakuraDataManager : Initializable {

    private val log = logger()

    private val data = listOf(
        TaskManager,
        HSEventManager
    )

    private val dataPath = File(PLATFORM.getConfigDir(), "${HiiroSakura.MOD_ID}/data").toPath()

    override fun init() {
        ClientLifecycleEvent.Starting.register {
            clientStart()
        }
        ClientLifecycleEvent.Stopping.register {
            onSave()
        }
    }

    fun clientStart() {
        runBlocking {
            log.info("Loading data...")
            data.forEach { data ->
                log.info("Loading data: ${data.key}")
                loadData(data)
            }
        }
    }

    fun onSave() {
        runBlocking {
            data.forEach {
                saveData(it)
            }
        }
    }

    fun asyncLoad() {
        data.forEach {
            ioLaunch { loadData(it) }
        }
    }

    private suspend fun loadData(data: HiiroSakuraData) = withContext(Dispatchers.IO) {
        runCatching {
            ConfigUtil.run {
                val file = configFile("${data.key}.json", dataPath)
                val json = readFileToString(file)
                data.deserialization(JsonDialect.decode(json).getOrThrow())
            }
        }.onFailure {
            saveData(data)
            log.warn(it)
        }
    }

    fun asyncSave() {
        data.forEach {
            ioLaunch { saveData(it) }
        }
    }

    private suspend fun saveData(data: HiiroSakuraData) = withContext(Dispatchers.IO) {
        runCatching {
            ConfigUtil.run {
                val file = configFile("${data.key}.json", dataPath)
                val str =JsonDialect.encode(data.serialization())
                writeToFile(str, file)
            }
        }.onFailure {
            log.warn(it)
        }
    }

}

