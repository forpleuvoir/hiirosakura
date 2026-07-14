package moe.forpleuvoir.hiirosakura.functional.script

import com.mojang.blaze3d.platform.InputConstants
import kotlinx.coroutines.delay
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.functional.event.HSEventManager
import moe.forpleuvoir.hiirosakura.input.InputSimulator
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.input.KeyCode
import moe.forpleuvoir.ibukigourd.input.MouseButton
import moe.forpleuvoir.ibukigourd.task.scheduleEndTick
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.sendMessage
import moe.forpleuvoir.nebula.common.util.defaultLaunch
import moe.forpleuvoir.nebula.config.Config
import moe.forpleuvoir.nebula.config.flat
import moe.forpleuvoir.nebula.config.path
import kotlin.time.Duration.Companion.milliseconds

@Suppress("unused")
interface CommonApi {

    companion object {

        val INSTANCE by lazy {
            object : CommonApi {
                override val logger: ModLogger by lazy { logger("CommonApi") }
            }
        }

        private val globalData = mutableMapOf<String, Any>()

    }

    val logger: ModLogger

    fun import(className: String): Class<*> {
        return HiiroSakura.javaClass.classLoader.loadClass(className)
    }

    fun sendMessage(message: String) {
        mc.sendMessage(message)
    }

    fun sendCommand(command: String) {
        if (command.startsWith("/")) {
            mc.sendMessage(command)
        } else {
            mc.sendMessage("/$command")
        }
    }

    fun sendNotification(title: String, content: String) {
        moe.forpleuvoir.hiirosakura.util.sendNotification(title, content)
    }

    fun toast(content: String) {
        ToastHandler.showContent { Text(Texts.inlineStyle(content)) }
    }

    fun onKey(keyCode: Int, scancode: Int, action: Int, modifiers: Int) {
        InputSimulator.onKey(keyCode, scancode, action, modifiers)
    }

    fun keyPress(keyCode: Int, duration: Long) {
        InputSimulator.keyPress(KeyCode.fromCode(keyCode), duration)
    }

    fun keyPress(key: String, duration: Long) {
        InputSimulator.keyPress(KeyCode.fromCode(InputConstants.getKey(key).value), duration)
    }

    fun mousePress(mouseCode: Int, duration: Long) {
        InputSimulator.mousePress(MouseButton.fromCode(mouseCode), duration)
    }

    fun mousePress(mouse: String, duration: Long) {
        InputSimulator.mousePress(MouseButton.fromCode(InputConstants.getKey(mouse).value), duration)
    }

    fun attack(duration: Long = 1) {
        InputSimulator.attack(duration)
    }

    fun use(duration: Long = 1) {
        InputSimulator.use(duration)
    }

    fun pickItem(duration: Long = 1) {
        InputSimulator.pickItem(duration)
    }

    fun moveForward(duration: Long = 1) {
        InputSimulator.moveForward(duration)
    }

    fun moveBack(duration: Long = 1) {
        InputSimulator.moveBack(duration)
    }

    fun moveLeft(duration: Long = 1) {
        InputSimulator.moveLeft(duration)
    }

    fun moveRight(duration: Long = 1) {
        InputSimulator.moveRight(duration)
    }

    fun jump(duration: Long = 1) {
        InputSimulator.jump(duration)
    }

    fun sneak(duration: Long = 1) {
        InputSimulator.sneak(duration)
    }

    fun sprint(duration: Long = 1) {
        InputSimulator.sprint(duration)
    }

    fun getGlobalData(key: String): Any? {
        return globalData[key]
    }

    fun setGlobalData(key: String, value: Any) {
        globalData[key] = value
    }

    fun delayLaunch(duration: Long, action: Runnable) = defaultLaunch {
        delay(duration.milliseconds)
        action.run()
    }

    fun scheduleStartTick(delay: Int, runner: Runnable) {
        mc.scheduleStartTick { _, _ -> runner.run() }
    }

    fun scheduleEndTick(delay: Int, runner: Runnable) {
        mc.scheduleEndTick { _, _ -> runner.run() }
    }

    @Suppress("UNCHECKED_CAST")
    fun setConfig(key: String, value: Any) {
        HSConfig.flat
            .find { it is Config<*> && it.path == key }
            ?.let {
                runCatching {
                    (it as? Config<Any>)?.setValue(value)
                }.onSuccess {
                    ToastHandler.showContent { Text(HSLang.Script.setConfigSuccess(key, value.toString())) }
                }.onFailure { t ->
                    ToastHandler.showContent { Text(HSLang.Script.setConfigFail(key, value.toString(), t.message)) }
                    logger.warn(t)
                }
            } ?: ToastHandler.showContent { Text(HSLang.Script.setConfigFailNotFound(key)) }
    }

    fun getConfig(key: String): Any? {
        return HSConfig.flat.find { it is Config<*> && it.path == key }
    }

    fun enableEvent(name: String, enable: Boolean) {
        HSEventManager.subscriberList.find { it.name == name }?.let {
            it.enabled = enable
            ToastHandler.showContent { Text(HSLang.Event.enableEvent(name, enable)) }
        } ?: ToastHandler.showContent { Text(HSLang.Event.enableEventNotFound(name)) }

    }

    fun editItem() {
//        mc.player?.let { player ->
//            if (!player.mainHandItem.isEmpty) {
//                ItemStackEditor { stack ->
//                    if (player.isCreative) player.inventory.selectedItem = stack
//                    else {
//                        mc.keyboardHandler.clipboard = stack.asCommand()
//                        Toast.showToast(HSLang.itemEditorCopyToCommand)
//                    }
//                }.open()
//            }
//        }
    }

}