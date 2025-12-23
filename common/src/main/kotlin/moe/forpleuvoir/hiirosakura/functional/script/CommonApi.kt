package moe.forpleuvoir.hiirosakura.functional.script

import kotlinx.coroutines.delay
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.functional.customdata.CustomData
import moe.forpleuvoir.hiirosakura.functional.event.HSEventManager
import moe.forpleuvoir.hiirosakura.input.InputSimulator
import moe.forpleuvoir.hiirosakura.util.flat
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.config.translationKey
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.task.scheduleEndTick
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.sendMessage
import moe.forpleuvoir.nebula.common.util.defaultLaunch
import moe.forpleuvoir.nebula.config.ConfigValue

@Suppress("unused")
interface CommonApi {

    companion object : CommonApi {

        private val globalData = mutableMapOf<String, Any>()

        override val logger = logger("CommonApi")

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
        Toast.showToast(text = content)
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

    fun setCustomData(key: String, value: Any) {
        CustomData.data[key] = value
    }

    fun getCustomData(key: String): Any? {
        return CustomData.data[key]
    }

    fun delayLaunch(duration: Long, action: Runnable) {
        defaultLaunch {
            delay(duration)
            action.run()
        }
    }

    fun scheduleStartTick(delay: Int, runner: Runnable) {
        mc.scheduleStartTick { _, _ -> runner.run() }
    }

    fun scheduleEndTick(delay: Int, runner: Runnable) {
        mc.scheduleEndTick { _, _ -> runner.run() }
    }

    @Suppress("UNCHECKED_CAST")
    fun setConfig(key: String, value: Any) {
        HSConfig.flat { it is ConfigValue<*> }
            .find { it.translationKey() == key }
            ?.let {
                runCatching {
                    (it as? ConfigValue<Any>)?.setValue(value)
                }.onSuccess {
                    Toast.showToast(HSLang.setConfigSuccess(key, value.toString()))
                }.onFailure { t ->
                    Toast.showToast(HSLang.setConfigFail(key, value.toString(), t.message))
                }
            } ?: Toast.showToast(HSLang.setConfigFailNotFound(key))
    }

    fun getConfig(key: String): Any? {
        return HSConfig.flat { it is ConfigValue<*> }
            .find { it.translationKey() == key }
    }

    fun enableEvent(name: String, enable: Boolean) {
        HSEventManager.subscriberList.find { it.name == name }?.let {
            it.enabled = enable
            Toast.showToast(HSLang.enableEvent(name, enable))
        } ?: Toast.showToast(HSLang.enableEventNotFound(name))
    }


}