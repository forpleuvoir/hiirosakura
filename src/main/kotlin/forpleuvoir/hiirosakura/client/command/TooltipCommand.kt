package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.getTranslatableTextKey
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import org.apache.commons.lang3.StringUtils

/**
 * 工具提示相关指令
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.command
 *
 * 文件名 TooltipCommand
 *
 * 创建时间 2021/6/17 21:58
 */
object TooltipCommand {
	private const val TYPE = "tooltip"
	private val AIR = SimpleCommandExceptionType(
		TranslatableText(getTranslatableTextKey(TYPE, "air"))
	)
	private val REMOVE_FAILED = DynamicCommandExceptionType { itemName: Any? ->
		TranslatableText(
			getTranslatableTextKey(
				TYPE,
				"removeFailed"
			),
			itemName
		)
	}

	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		dispatcher.register(ClientCommandManager.literal(COMMAND_PREFIX + TYPE)
			.then(ClientCommandManager.literal("add")
				.then(ClientCommandManager.argument("item", ItemStackArgumentType.itemStack())
					.then(ClientCommandManager.argument("tip", StringArgumentType.string())
						.executes { add(it) }
					)
				)
				.then(ClientCommandManager.argument("tip", StringArgumentType.string())
					.executes { addMainHandItem(it) }
				))
			.then(ClientCommandManager.literal("remove")
				.then(ClientCommandManager.argument("item", ItemStackArgumentType.itemStack())
					.then(ClientCommandManager.argument("index", IntegerArgumentType.integer(-1))
						.executes { remove(it) }
					)
				)
				.then(ClientCommandManager.argument("index", IntegerArgumentType.integer(-1))
					.executes { removeMainHandItem(it) }
				)
			)
		)
	}

	@Throws(CommandSyntaxException::class)
	private fun addMainHandItem(context: CommandContext<FabricClientCommandSource>): Int {
		val tip = StringArgumentType.getString(context, "tip")
		val item = context.source.player.mainHandStack
		add(item, tip, context)
		return 1
	}

	@Throws(CommandSyntaxException::class)
	private fun add(item: ItemStack, tip: String, context: CommandContext<FabricClientCommandSource>) {
		if (item.item == Items.AIR) throw AIR.create()
		HiiroSakuraData.TOOLTIP.add(item, tip)
		val feedback = LiteralText("").append(item.toHoverableText())
		feedback.append(TranslatableText(getTranslatableTextKey(TYPE, "add")))
		feedback.append(LiteralText(tip))
		context.source.sendFeedback(feedback)
	}

	@Throws(CommandSyntaxException::class)
	private fun add(context: CommandContext<FabricClientCommandSource>): Int {
		val item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false)
		val tip = StringArgumentType.getString(context, "tip")
		add(item, tip, context)
		return 1
	}

	@Throws(CommandSyntaxException::class)
	private fun removeMainHandItem(
		context: CommandContext<FabricClientCommandSource>
	): Int {
		val index = IntegerArgumentType.getInteger(context, "index")
		val item = context.source.player.mainHandStack
		remove(item, index, context)
		return 1
	}

	@Throws(CommandSyntaxException::class)
	private fun remove(context: CommandContext<FabricClientCommandSource>): Int {
		val item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false)
		val index = IntegerArgumentType.getInteger(context, "index")
		remove(item, index, context)
		return 1
	}

	@Throws(CommandSyntaxException::class)
	private fun remove(item: ItemStack, index: Int, context: CommandContext<FabricClientCommandSource>) {
		if (item.item == Items.AIR) throw REMOVE_FAILED.create(item.name)
		val remove = HiiroSakuraData.TOOLTIP.remove(item, index)
		if (!StringUtils.isEmpty(remove)) {
			val feedback = LiteralText("").append(item.toHoverableText())
			feedback.append(TranslatableText(getTranslatableTextKey(TYPE, "remove")))
			feedback.append(LiteralText(remove))
			context.source.sendFeedback(feedback)
		} else {
			throw REMOVE_FAILED.create(item.name)
		}
	}
}