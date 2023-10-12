package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.context.CommandContext
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.ibuki_gourd.common.mText
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.TranslatableTextContent

/**
 * 调整gamma值指令
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.command
 *
 * 文件名 GammaCommand
 *
 * 创建时间 2021/6/10 22:39
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
        context.source.client.options.gamma.value = value
        context.source.sendFeedback(
            TranslatableTextContent(
                "hiirosakura.command.feedback.gamma",
                null,
                arrayOf(value)
            ).mText
        )
        return 1
    }
}