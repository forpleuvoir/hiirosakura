package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.getTranslatableTextKey
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.text.TranslatableText

/**
 * 快捷发送聊天消息指令
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.command
 *
 * #class_name QuickChatMessageSendCommand
 *
 * #create_time 2021/6/17 0:10
 */
object QuickChatMessageSendCommand {
	private const val TYPE = "qcms"
	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		dispatcher.register(
			ClientCommandManager.literal(COMMAND_PREFIX + TYPE)
				.then(ClientCommandManager.literal("add")
					.then(ClientCommandManager.argument("remark", StringArgumentType.string())
						.then(ClientCommandManager.argument("messageStr", StringArgumentType.string())
							.executes { add(it) }
						)
					)
				)
				.then(ClientCommandManager.literal("show")
					.executes { show(it) }
				)
				.then(ClientCommandManager.literal("remove")
					.then(ClientCommandManager.argument("remark", StringArgumentType.string())
						.suggests { _: CommandContext<FabricClientCommandSource>, b: SuggestionsBuilder ->
							CommandSource.suggestMatching(
								HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
									.keySet, b
							)
						}
						.executes { remove(it) }
					)
				)
				.then(ClientCommandManager.literal("rename")
					.then(ClientCommandManager.argument("remark", StringArgumentType.string())
						.suggests { _: CommandContext<FabricClientCommandSource>, b: SuggestionsBuilder? ->
							CommandSource.suggestMatching(
								HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
									.keySet, b
							)
						}
						.then(ClientCommandManager.argument("newRemark", StringArgumentType.string())
							.executes { rename(it) }
						)
					)
				)
				.then(ClientCommandManager.literal("reset")
					.then(ClientCommandManager.argument("remark", StringArgumentType.string())
						.suggests { _: CommandContext<FabricClientCommandSource>, b: SuggestionsBuilder? ->
							CommandSource.suggestMatching(
								HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
									.keySet, b
							)
						}
						.then(ClientCommandManager.argument("newValue", StringArgumentType.string())
							.executes { reset(it) }
						)
					)
				)
		)
	}

	private fun add(context: CommandContext<FabricClientCommandSource>): Int {
		val remark = StringArgumentType.getString(context, "remark")
		val messageStr = StringArgumentType.getString(context, "messageStr")
		if (!HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.add(remark, messageStr)) {
			context.source.sendFeedback(TranslatableText(getTranslatableTextKey(TYPE, "add.fail"), remark))
			return 1
		}
		context.source.sendFeedback(TranslatableText(getTranslatableTextKey(TYPE, "add"), remark))
		return 1
	}

	private fun show(context: CommandContext<FabricClientCommandSource>): Int {
		context.source.sendFeedback(HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.asText)
		return 1
	}

	private fun remove(context: CommandContext<FabricClientCommandSource>): Int {
		val remark = StringArgumentType.getString(context, "remark")
		HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.remove(remark)
		context.source.sendFeedback(TranslatableText(getTranslatableTextKey(TYPE, "remove")))
		return 1
	}

	private fun rename(context: CommandContext<FabricClientCommandSource>): Int {
		val remark = StringArgumentType.getString(context, "remark")
		val newRemark = StringArgumentType.getString(context, "newRemark")
		HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.rename(remark, newRemark)
		context.source.sendFeedback(TranslatableText(getTranslatableTextKey(TYPE, "rename")))
		return 1
	}

	private fun reset(context: CommandContext<FabricClientCommandSource>): Int {
		val remark = StringArgumentType.getString(context, "remark")
		val newValue = StringArgumentType.getString(context, "newValue")
		HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.put(remark, newValue)
		context.source.sendFeedback(TranslatableText(getTranslatableTextKey(TYPE, "reset")))
		return 1
	}
}