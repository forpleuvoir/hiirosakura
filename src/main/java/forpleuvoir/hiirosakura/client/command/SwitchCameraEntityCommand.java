package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import forpleuvoir.hiirosakura.client.feature.cameraentity.SwitchCameraEntity;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.command.argument.EntityArgumentType;

import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name SwitchCameraEntityCommand
 * <p>#create_time 2021/6/24 20:56
 */
public class SwitchCameraEntityCommand {
    public static final String TYPE = "sce";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(COMMAND_PREFIX + TYPE)
                                    .then(argument("player", StringArgumentType.string())
                                                  .suggests((context, builder) ->
                                                                    suggestMatching(
                                                                            SwitchCameraEntity.INSTANCE
                                                                                    .getPlayersSuggest(),
                                                                            builder
                                                                    )
                                                  )
                                                  .executes(SwitchCameraEntityCommand::execute)
                                    )
        );
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        var playerName = StringArgumentType.getString(context,"player");
        SwitchCameraEntity.INSTANCE.switchOtherPlayer(playerName);
        return 1;
    }
}
