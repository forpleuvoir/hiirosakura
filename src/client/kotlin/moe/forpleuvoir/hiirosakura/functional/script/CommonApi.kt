package moe.forpleuvoir.hiirosakura.functional.script

import moe.forpleuvoir.hiirosakura.input.InputSimulator
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.sendMessage

interface CommonApi {

    companion object {

        private val globalData = mutableMapOf<String, Any>()

        @JvmStatic
        val INSTANCE = object : CommonApi {}
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

}