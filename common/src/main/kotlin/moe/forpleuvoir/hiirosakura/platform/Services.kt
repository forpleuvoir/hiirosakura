package moe.forpleuvoir.hiirosakura.platform

import moe.forpleuvoir.hiirosakura.platform.services.PlatformHelper
import moe.forpleuvoir.hiirosakura.util.logger
import java.util.*

object Services {

    private val logger = logger()

    val PLATFORM = load(PlatformHelper::class.java)

    fun <T> load(clazz: Class<T>): T {
        val loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow {
                kotlin.IllegalStateException("Failed to load service for ${clazz.name}")
            }
        logger.debug("loaded {} for service {}", loadedService, clazz)
        return loadedService
    }

}

internal val PLATFORM get() = Services.PLATFORM

