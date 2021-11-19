package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.entity.EquipmentSlot
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText


/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.command

 * 文件名 NbtCommand

 * 创建时间 2021/11/19 23:18

 * @author forpleuvoir

 */
object NbtCommand {
    private const val TYPE = "nbt"

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            literal(HiiroSakuraClientCommand.COMMAND_PREFIX + TYPE)
                .executes { get(it) }
                .then(literal("all")
                    .executes { all(it) }
                )
        )
    }

    private fun get(context: CommandContext<FabricClientCommandSource>): Int {
        val player = context.source.player
        player.mainHandStack.nbt?.let { nbt ->
            context.source.sendFeedback(LiteralText(nbt.asString()).styled { style ->
                style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nbt.asString()))
                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, TranslatableText("chat.copy.click")))
            })
        }
        return 1
    }

    private fun all(context: CommandContext<FabricClientCommandSource>): Int {
        val player = context.source.player
        val stack = player.mainHandStack
        stack.nbt?.let { nbt ->
            if (!stack.isEmpty) {
                EquipmentSlot.values().forEach {
                    stack.getAttributeModifiers(it).forEach { t, u ->
                        stack.addAttributeModifier(t, u, it)
                    }
                }
            }
            context.source.sendFeedback(LiteralText(nbt.asString()).styled { style ->
                style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nbt.asString()))
                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, TranslatableText("chat.copy.click")))
            })
        }
        return 1
    }
}
