package forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput.Key.*
import forpleuvoir.hiirosakura.client.mixin.MixinMinecraftClientInterface
import net.minecraft.client.MinecraftClient

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor

 * 文件名 JavaScriptInterfaceImpl

 * 创建时间 2022/1/19 14:14

 * @author forpleuvoir

 */
class JavaScriptInterfaceImpl : JavaScriptInterface {
    override fun forward(tick: Int) {
        input[FORWARD] = tick
    }

    override fun back(tick: Int) {
        input[BACK] = tick
    }

    override fun left(tick: Int) {
        input[LEFT] = tick
    }

    override fun right(tick: Int) {
        input[RIGHT] = tick
    }

    override fun jump(tick: Int) {
        input[JUMP] = tick
    }

    override fun sneak(tick: Int) {
        input[SNEAK] = tick
    }

    override fun attack(tick: Int) {
        input[ATTACK] = tick
    }

    override fun use(tick: Int) {
        input[USE] = tick
    }

    override fun pickItem(tick: Int) {
        input[PICK_ITEM] = tick
    }

    override fun doAttack() {
        (mc as MixinMinecraftClientInterface).callDoAttack()
    }

    override fun doItemUse() {
        (mc as MixinMinecraftClientInterface).callDoItemUse()
    }

    override fun doItemPick() {
        (mc as MixinMinecraftClientInterface).callDoItemPick()
    }

    override fun joinServer(address: String) {
        TODO("Not yet implemented")
    }

    override fun joinServer(address: String, maxConnect: Int) {
        TODO("Not yet implemented")
    }

    override fun sendChatMessage(message: String) {
        hs.sendMessage(message)
    }

    override fun sendCommand(command: String) {
        mc.player?.networkHandler?.sendCommand(if (command.startsWith("/")) command.substring(1) else command)
    }

    override fun dropItem(slot: Int, all: Boolean) {
    }

    override fun dropItem(name: String, all: Boolean) {
    }

    companion object {
        private val mc = MinecraftClient.getInstance()
        private val hs = HiiroSakuraClient
        private val input = AnalogInput
    }
}