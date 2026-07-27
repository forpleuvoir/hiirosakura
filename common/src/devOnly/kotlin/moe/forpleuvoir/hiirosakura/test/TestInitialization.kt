package moe.forpleuvoir.hiirosakura.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditor
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditorState
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.KeyEnvironment
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.platform.services.ModInitialization
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.ui.openComposeScreen
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.util.logger

class TestInitialization : ModInitialization {
    internal val log = logger("HS TEST")

    override fun init() {
        TestCommand.init()
        InputHandler.apply {
            register(Keyboard.KP_1) {
                testItemEditor()
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
                Keyboard.LEFT_CONTROL, Keyboard.KP_3, defaultSetting = KeybindSetting(
                    env = KeyEnvironment.Any
                )
            ) {
                openComposeScreen {
                    IbukiGourdTheme {
                        var showDialog by remember { mutableStateOf(true) }
                        var text by remember { mutableStateOf(Literal("待到秋来九月八，我花开后百花杀。\n冲天香阵透长安，满城尽带黄金甲。")) }
                        Button(onClick = {
                            showDialog = true
                        }) {
                            Text("点我")
                        }

                        if (showDialog) {
                            val state = remember { RichTextEditorState.fromMcText(text) }
                            FlexibleDialog(
                                { showDialog = false },
                                modifier = Modifier.padding(24.dp).height(520.dp).width(720.dp),
                                onConfirmRequest = {
                                    text = state.mcText
                                    true
                                },
                                title = {
                                    Text("文本编辑器")
                                },
                                content = {
                                    RichTextEditor(
                                        state = state
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
