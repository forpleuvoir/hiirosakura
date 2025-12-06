package moe.forpeluvoir.hiirosakura.platform

import moe.forpleuvoir.hiirosakura.platform.services.PlatformHelper
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.fml.loading.FMLPaths
import java.io.File

class NeoforgePlatformHelper: PlatformHelper {

    override fun getPlatformName(): String = "Neoforge"

    override fun isModLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)

    override fun isDevEnvironment(): Boolean = !FMLLoader.getCurrent().isProduction

    override fun getConfigDir(): File = FMLPaths.CONFIGDIR.get().toFile()
}