package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.context.CommandContext
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.text.TranslatableText

/**
 * 调整gamma值指令
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.command
 *
 * #class_name GammaCommand
 *
 * #create_time 2021/6/10 22:39
 */
object GammaCommand {
	//注册指令
	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		dispatcher.register(
			ClientCommandManager.literal(COMMAND_PREFIX + "gamma")
				.then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(0.0, 100.0))
					.executes { set(it) })
		)
	}

	/**
	 * 修改伽马值
	 *
	 * 用法 /hs:gamma [Double]
	 *
	 * @param context 指令上下文
	 * @return 执行次数  [Double]
	 */
	fun set(context: CommandContext<FabricClientCommandSource>): Int {
		val value = DoubleArgumentType.getDouble(context, "value")
		context.source.client.options.gamma = value
		context.source.sendFeedback(TranslatableText("hiirosakura.command.feedback.gamma", value))
		return 1
	}
}