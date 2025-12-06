package moe.forpleuvoir.hiirosakura.platform

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.platform.services.PlatformHelper
import moe.forpleuvoir.ibukigourd.util.ModLogger
import net.fabricmc.loader.api.FabricLoader
import java.io.File

class FabricPlatformHelper : PlatformHelper {

    companion object {

        private val logger = ModLogger("FabricPlatformHelper", HiiroSakura.MOD_NAME)

        private val loader: FabricLoader by lazy { FabricLoader.getInstance() }

    }

    override fun getPlatformName(): String = "Fabric"

    override fun isModLoaded(modId: String): Boolean = loader.isModLoaded(modId)

    override fun isDevEnvironment(): Boolean = loader.isDevelopmentEnvironment

    override fun getConfigDir(): File = loader.configDir.toFile()

}