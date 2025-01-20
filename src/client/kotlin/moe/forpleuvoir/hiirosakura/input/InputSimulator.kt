package moe.forpleuvoir.hiirosakura.input

import moe.forpleuvoir.ibukigourd.event.events.client.ClientTickEvent
import moe.forpleuvoir.ibukigourd.input.KeyCode
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.input.Mouse
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import net.minecraft.client.Keyboard as McKeyBoard
import net.minecraft.client.Mouse as McMouse

@EventSubscriber
object InputSimulator {

    private val client: MinecraftClient get() = MinecraftClient.getInstance()

    private val windowsHandler: Long get() = client.window.handle

    private val keyBoard: McKeyBoard get() = client.keyboard

    private val mouse: McMouse get() = client.mouse

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
        mc.execute { keyBoard.onKey(windowsHandler, keyCode, scancode, action, modifiers) }
    }

    fun keyPress(keyCode: KeyCode, duration: Long = 1) {
        keyPressed[keyCode] = duration.coerceAtLeast(1) to duration.coerceAtLeast(1)
    }

    fun onMouseButton(button: Int, action: Int, mods: Int) {
        mc.execute { mouse.onMouseButton(windowsHandler, button, action, mods) }
    }

    fun mousePress(button: Mouse, duration: Long = 1) {
        mousePressed[button] = duration.coerceAtLeast(1) to duration.coerceAtLeast(1)
    }

    fun attack(duration: Long = 1) {
        mousePress(Mouse.fromCode(client.options.attackKey.boundKey.code), duration)
    }

    fun use(duration: Long = 1) {
        mousePress(Mouse.fromCode(client.options.useKey.boundKey.code), duration)
    }

    fun pickItem(duration: Long = 1) {
        mousePress(Mouse.fromCode(client.options.pickItemKey.boundKey.code), duration)
    }

    fun moveForward(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.forwardKey.boundKey.code), duration)
    }

    fun moveBack(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.backKey.boundKey.code), duration)
    }

    fun moveLeft(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.leftKey.boundKey.code), duration)
    }

    fun moveRight(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.rightKey.boundKey.code), duration)
    }

    fun jump(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.jumpKey.boundKey.code), duration)
    }

    fun sneak(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.sneakKey.boundKey.code), duration)
    }

    fun sprint(duration: Long = 1) {
        keyPress(Keyboard.fromCode(client.options.sprintKey.boundKey.code), duration)
    }

}
