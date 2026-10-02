package moe.forpleuvoir.hiirosakura.test

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import moe.forpleuvoir.compose_minecraft.platform.render.text.TextRenderConfig
import net.minecraft.network.chat.Component
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.ibukigourd.command.clientSource
import moe.forpleuvoir.ibukigourd.command.dsl.registerCommand
import moe.forpleuvoir.ibukigourd.config.exportTranslateKeys
import moe.forpleuvoir.ibukigourd.event.events.client.ClientCommandRegistrationEvent
import moe.forpleuvoir.ibukigourd.lang.TranslationRecorder
import moe.forpleuvoir.ibukigourd.mod.config.IGConfig
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.nebula.common.api.Initializable
import net.minecraft.commands.SharedSuggestionProvider
import kotlin.io.path.Path
import moe.forpleuvoir.ibukigourd.util.mc

object TestCommand : Initializable {

    override fun init() {
        ClientCommandRegistrationEvent.register {
            testCommand()
        }
    }

    context(context: CommandDispatcher<out SharedSuggestionProvider>)
    fun testCommand() = registerCommand("hstest") {
        // 多物品选择器验收屏:点物品累积选中、下方容器可移除、确认才写回、上限 5 的实例
        "multi_item_selector" {
            execute {
                mc.execute {
                    openMultiItemSelectorTest()
                }
            }
        }
        // 切换 CMP 的文本行盒/基线调试绘制（TextRenderConfig.debugTextBounds）
        "text_bounds" {
            execute {
                val enabled = !TextRenderConfig.debugTextBounds
                TextRenderConfig.debugTextBounds = enabled
                mc.player?.sendSystemMessage(Component.literal("debugTextBounds = $enabled"))
            }
        }
        "config_keys" {
            execute {
                val recorder = TranslationRecorder(false, keepExisting = true)
                recorder.categorizer = { "config" }
                recorder.addFilter { true }
                HSConfig.exportTranslateKeys().forEach {
                    recorder.record(it)
                }
                recorder.dump(Path("../../../common/src/devOnly/lang"))
            }
        }
        "config_keys_task" {
            execute {
                val recorder = TranslationRecorder(false, keepExisting = true)
                recorder.categorizer = { "task_config" }
                recorder.addFilter { true }
                TaskManager.Config.exportTranslateKeys().forEach {
                    recorder.record(it)
                }
                recorder.dump(Path("../../../common/src/devOnly/lang"))
            }
        }
    }
}