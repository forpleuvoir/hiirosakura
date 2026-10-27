package moe.forpleuvoir.hiirosakura.input

import com.mojang.blaze3d.platform.InputConstants
import moe.forpleuvoir.ibukigourd.event.events.client.ClientTickEvent
import moe.forpleuvoir.ibukigourd.input.KeyCode
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.input.MouseButton
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.api.Initializable
import net.minecraft.client.KeyboardHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.MouseHandler
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonInfo
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button

object InputSimulator : Initializable {

    private val client: Minecraft get() = Minecraft.getInstance()

    private val windowsHandler: Long get() = client.window.handle()

    private val keyBoard: KeyboardHandler get() = client.keyboardHandler

    private val mouse: MouseHandler get() = client.mouseHandler

    private val keyPressed: MutableMap<KeyCode, Pair<Long, Long>> = mutableMapOf()

    private val mousePressed: MutableMap<MouseButton, Pair<Long, Long>> = mutableMapOf()

    override fun init() {
        ClientTickEvent.TickEnd.register { tick() }
    }

    private fun tick() {
        val keyRemoveList = mutableListOf<KeyCode>()
        keyPressed.forEach { (keyCode, value) ->
            val (duration, lastDuration) = value

            val action = if (lastDuration == duration) {
                InputConstants.PRESS
            } else if (lastDuration > 0) {
                InputConstants.REPEAT
            } else {
                keyRemoveList.add(keyCode)
                InputConstants.RELEASE
            }

            onKey(keyCode.code, 0, action, 0)
            keyPressed[keyCode] = duration to lastDuration - 1
        }
        keyRemoveList.forEach { keyPressed.remove(it) }


        val mouseRemoveList = mutableListOf<KeyCode>()
        mousePressed.forEach { (mouse, value) ->
            val (duration, lastDuration) = value

            val action = if (lastDuration == duration) {
                InputConstants.PRESS
            } else if (lastDuration > 0) {
                InputConstants.REPEAT
            } else {
                mouseRemoveList.add(mouse)
                InputConstants.RELEASE
            }

            if (action != InputConstants.REPEAT) onMouseButton(mouse.code, action, 0)
            mousePressed[mouse] = duration to lastDuration - 1
        }
        mouseRemoveList.forEach { mousePressed.remove(it) }

    }

    fun onKey(keyCode: Int, scancode: Int, action: Int, modifiers: Int) {
        mc.execute { keyBoard.keyPress(windowsHandler, action, KeyEvent(keyCode, scancode, modifiers)) }
    }

    fun keyPress(keyCode: KeyCode, duration: Long = 1) {
        keyPressed[keyCode] = duration.coerceAtLeast(1) to duration.coerceAtLeast(1)
    }

    fun onMouseButton(button: Int, action: Int, mods: Int) {
        mc.execute { mouse.onButton(windowsHandler, MouseButtonInfo(button, mods), action) }
    }

    fun mousePress(button: MouseButton, duration: Long = 1) {
        mousePressed[button] = duration.coerceAtLeast(1) to duration.coerceAtLeast(1)
    }

    fun attack(duration: Long = 1) {
        MouseButton.fromCode(client.options.keyAttack.key.value)?.let { mousePress(it, duration) }
    }

    fun use(duration: Long = 1) {
        MouseButton.fromCode(client.options.keyUse.key.value)?.let { mousePress(it, duration) }
    }

    fun pickItem(duration: Long = 1) {
        MouseButton.fromCode(client.options.keyPickItem.key.value)?.let { mousePress(it, duration) }
    }

    fun moveForward(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.keyUp.key.value), duration)
    }

    fun moveBack(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.keyDown.key.value), duration)
    }

    fun moveLeft(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.keyLeft.key.value), duration)
    }

    fun moveRight(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.keyRight.key.value), duration)
    }

    fun jump(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.keyJump.key.value), duration)
    }

    fun sneak(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.keyShift.key.value), duration)
    }

    fun sprint(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.keySprint.key.value), duration)
    }


}