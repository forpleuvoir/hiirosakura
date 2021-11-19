package forpleuvoir.hiirosakura.client.feature.task.executor.base

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput.Key.*
import forpleuvoir.hiirosakura.client.feature.task.TimeTask.Companion.once
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler
import forpleuvoir.hiirosakura.client.feature.task.executor.SimpleExecutor
import forpleuvoir.hiirosakura.client.mixin.MixinMinecraftClientInterface
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.network.ServerAddress
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket

/**
 * js脚本接口实现
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task.executor.base
 *
 * #class_name JavaScriptInterface
 *
 * #create_time 2021/7/27 23:20
 */
class JavaScriptInterface : IJavaScriptInterface {
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
        joinServer(address, 5)
    }

    override fun joinServer(address: String, maxConnect: Int) {
        if (mc.world == null && maxConnect > ServerInfoUtil.disConnectCounter) {
            val multiplayerScreen = MultiplayerScreen(TitleScreen())
            mc.setScreen(multiplayerScreen)
            TimeTaskHandler.INSTANCE!!.addTask(
                once(
                    SimpleExecutor {
                        mc.disconnect()
                        ConnectScreen.connect(multiplayerScreen, mc, ServerAddress.parse(address), null)
                    },
                    15,
                    "#joinServer"
                )
            )
        }
    }

    override fun sendChatMessage(message: String) {
        hs.sendMessage(message)
    }

    override fun dropItem(slot: Int, all: Boolean) {
        val player = mc.player
        val inventory = player?.inventory
        player?.dropSelectedItem(all)
        inventory?.updateItems()
        mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(inventory!!.selectedSlot))
    }

    override fun dropItem(name: String, all: Boolean) {
        TODO("Not yet implemented")
    }

    companion object {
        private val mc = MinecraftClient.getInstance()
        private val hs = HiiroSakuraClient
        private val input = AnalogInput
    }
}