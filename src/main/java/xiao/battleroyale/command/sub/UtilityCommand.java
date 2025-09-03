package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.common.server.utility.SurvivalLobby;

import static xiao.battleroyale.command.CommandArg.*;

public class UtilityCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        // 不需要权限
        LiteralArgumentBuilder<CommandSourceStack> utilityCommand = Commands.literal(UTILITY)
                // 生存模式大厅
                .then(Commands.literal(SURVIVAL_LOBBY)
                        .executes(UtilityCommand::survivalLobby))
                .then(Commands.literal(TO_SURVIVAL_LOBBY)
                        .executes(UtilityCommand::toSurvivalLobby));

        // 需要权限

        return utilityCommand;
    }

    private static int survivalLobby(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            ServerPlayer player = context.getSource().getPlayerOrException();
            SurvivalLobby.get().sendLobbyInfo(player);
        } else {
            ServerLevel serverLevel = source.getLevel();
            SurvivalLobby.get().sendLobbyInfo(serverLevel);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int toSurvivalLobby(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            ServerPlayer player = context.getSource().getPlayerOrException();
            SurvivalLobby.get().teleportToLobby(player);
            return Command.SINGLE_SUCCESS;
        } else {
            return 0;
        }
    }
}
