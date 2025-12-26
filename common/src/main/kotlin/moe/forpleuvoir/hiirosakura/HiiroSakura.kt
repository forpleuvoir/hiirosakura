package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.hiirosakura.util.logger

object HiiroSakura {

    val logger = logger()

    const val MOD_ID: String = "hiirosakura"

    const val MOD_NAME: String = "HiiroSakura"

    fun init() {
        logger.info("$MOD_ID,platform:{},env:{}", PLATFORM.getPlatformName(), PLATFORM.getEnvironmentName())
    }


}