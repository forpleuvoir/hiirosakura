package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import forpleuvoir.hiirosakura.client.HiiroSakuraClient.mc
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand
import forpleuvoir.hiirosakura.client.util.asString
import forpleuvoir.ibuki_gourd.common.mText
import forpleuvoir.ibuki_gourd.common.tText
import forpleuvoir.ibuki_gourd.utils.mText
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.entity.EquipmentSlot
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.registry.Registry


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
                .then(literal("withId")
                    .executes { withId(it) }
                )
                .then(literal("withProperties")
                    .executes { withProperties(it) }
                )
        )
    }


    private fun withProperties(context: CommandContext<FabricClientCommandSource>): Int {
        mc.crosshairTarget?.let {
            if (it.type == HitResult.Type.BLOCK) {
                val blockPos = (mc.crosshairTarget as BlockHitResult).blockPos
                mc.world?.let { clientWorld ->
                    val blockState = clientWorld.getBlockState(blockPos)
                    val str = blockState.asString()
                    context.source.sendFeedback(str.mText.styled { style ->
                        style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, str))
                            .withHoverEvent(
                                HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    "chat.copy.click".tText().mText
                                )
                            )
                    })
                }
            }
        }
        return 1
    }

    private fun withId(context: CommandContext<FabricClientCommandSource>): Int {
        val player = context.source.player
        val stack = player.mainHandStack
        val id = Registry.ITEM.getId(stack.item).toString() + if (stack.nbt == null) "" else stack.nbt!!.asString()
        context.source.sendFeedback(id.mText.styled { style ->
            style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id))
                .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, "chat.copy.click".tText().mText))
        })

        return 1
    }

    private fun get(context: CommandContext<FabricClientCommandSource>): Int {
        val player = context.source.player
        player.mainHandStack.nbt?.let { nbt ->
            context.source.sendFeedback(nbt.asString().mText.styled { style ->
                style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nbt.asString()))
                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, "chat.copy.click".tText().mText))
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
            context.source.sendFeedback(nbt.asString().mText.styled { style ->
                style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nbt.asString()))
                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, "chat.copy.click".tText().mText))
            })
        }
        return 1
    }
}
