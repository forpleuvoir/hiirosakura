package moe.forpleuvoir.hiirosakura

import net.fabricmc.api.ClientModInitializer

object FabricHiiroSakuraClient : ClientModInitializer {
    override fun onInitializeClient() {
        HiiroSakuraClient.init()
    }
}