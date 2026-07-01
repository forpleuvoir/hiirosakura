package moe.forpleuvoir.hiirosakura.test

import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.platform.services.ModInitialization
import moe.forpleuvoir.ibukigourd.util.logger

class TestInitialization : ModInitialization {
    internal val log = logger("HS TEST")

    override fun init() {

        InputHandler.apply {
            register(Keyboard.KP_1) {
                openItemBrowser()
            }

        }
    }
}
