package moe.forpleuvoir.hiirosakura.platform.services

import java.io.File

interface PlatformHelper {

    fun getPlatformName(): String

    fun isModLoaded(modId: String): Boolean

    fun isDevEnvironment(): Boolean

    fun getEnvironmentName(): String {
        return if (isDevEnvironment()) "development" else "production"
    }

    fun getConfigDir(): File

}