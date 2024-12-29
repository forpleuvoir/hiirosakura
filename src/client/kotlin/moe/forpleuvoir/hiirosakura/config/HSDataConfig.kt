package moe.forpleuvoir.hiirosakura.config

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.ibukigourd.config.ClientModConfigManager
import moe.forpleuvoir.ibukigourd.config.ModConfig

@ModConfig(name = "data")
object HSDataConfig : ClientModConfigManager(HiiroSakura.metadata, "data") {

    init {
        addConfig(TaskManager.Config)
    }


}