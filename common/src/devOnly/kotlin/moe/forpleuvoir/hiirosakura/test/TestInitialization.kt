package moe.forpleuvoir.hiirosakura.test

import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.KeyEnvironment
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.platform.services.ModInitialization
import moe.forpleuvoir.ibukigourd.util.logger

class TestInitialization : ModInitialization {
    internal val log = logger("HS TEST")

    override fun init() {
        TestCommand.init()
        InputHandler.apply {
            register(Keyboard.KP_1) {
                openItemBrowser()
            }
            register(
                Keyboard.KP_2, defaultSetting = KeybindSetting(
                    env = KeyEnvironment.Any
                )
            ) {
                openBlockMacherEditor()
            }
            register(
                Keyboard.KP_3, defaultSetting = KeybindSetting(
                    env = KeyEnvironment.Any
                )
            ) {
                openItemMacherEditor()
            }
            register(
                Keyboard.KP_4, defaultSetting = KeybindSetting(
                    env = KeyEnvironment.Any
                )
            ) {
                openRadialMenuTest()
            }

        }
    }
}
