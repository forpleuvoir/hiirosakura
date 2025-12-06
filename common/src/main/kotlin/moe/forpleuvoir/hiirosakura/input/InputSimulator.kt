package moe.forpleuvoir.hiirosakura.input

import moe.forpleuvoir.ibukigourd.event.events.client.ClientTickEvent
import moe.forpleuvoir.ibukigourd.input.KeyCode
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.input.Mouse
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import net.minecraft.client.KeyboardHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.MouseHandler
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonInfo
import org.lwjgl.glfw.GLFW

@EventSubscriber
object InputSimulator {

    private val client: Minecraft get() = Minecraft.getInstance()

    private val windowsHandler: Long get() = client.window.handle()

    private val keyBoard: KeyboardHandler get() = client.keyboardHandler

    private val mouse: MouseHandler get() = client.mouseHandler

    private val keyPressed: MutableMap<KeyCode, Pair<Long, Long>> = mutableMapOf()

    private val mousePressed: MutableMap<Mouse, Pair<Long, Long>> = mutableMapOf()

    @Subscriber
    fun tick(event: ClientTickEvent.ClientTickEndEvent) {
        val keyRemoveList = mutableListOf<KeyCode>()
        keyPressed.forEach { (keyCode, value) ->
            val (duration, lastDuration) = value

            val action = if (lastDuration == duration) {
                GLFW.GLFW_PRESS
            } else if (lastDuration > 0) {
                GLFW.GLFW_REPEAT
            } else {
                keyRemoveList.add(keyCode)
                GLFW.GLFW_RELEASE
            }

            onKey(keyCode.code, 0, action, 0)
            keyPressed[keyCode] = duration to lastDuration - 1
        }
        keyRemoveList.forEach { keyPressed.remove(it) }

        val mouseRemoveList = mutableListOf<KeyCode>()
        mousePressed.forEach { (mouse, value) ->
            val (duration, lastDuration) = value

            val action = if (lastDuration == duration) {
                GLFW.GLFW_PRESS
            } else if (lastDuration > 0) {
                GLFW.GLFW_REPEAT
            } else {
                mouseRemoveList.add(mouse)
                GLFW.GLFW_RELEASE
            }
            if (action != GLFW.GLFW_REPEAT) onMouseButton(mouse.code, action, 0)
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

    fun mousePress(button: Mouse, duration: Long = 1) {
        mousePressed[button] = duration.coerceAtLeast(1) to duration.coerceAtLeast(1)
    }

    fun attack(duration: Long = 1) {
        mousePress(Mouse.fromCode(client.options.keyAttack.key.value), duration)
    }

    fun use(duration: Long = 1) {
        mousePress(Mouse.fromCode(client.options.keyUse.key.value), duration)
    }

    fun pickItem(duration: Long = 1) {
        mousePress(Mouse.fromCode(client.options.keyPickItem.key.value), duration)
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
