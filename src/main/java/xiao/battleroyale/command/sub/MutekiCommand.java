package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.effect.muteki.MutekiManager;

import java.util.Collection;

import static xiao.battleroyale.command.CommandArg.*;

public class MutekiCommand {

    private static final int DEFAULT_TIME = 20 * 5;

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> mutekiCommand = Commands.literal(MUTEKI);

        RequiredArgumentBuilder<CommandSourceStack, Integer> timeArgument = Commands.argument(TIME, IntegerArgumentType.integer(1, MutekiManager.MAX_MUTEKI_TIME));
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> playerArgument = Commands.argument(PLAYER, EntityArgument.players());

        // muteki <players> <time>
        timeArgument.executes(MutekiCommand::executeAddMutekiWithTimeForPlayers);
        // muteki <players>
        playerArgument.executes(MutekiCommand::executeAddMutekiDefaultTimeForPlayers);
        playerArgument.then(timeArgument);

        // muteki clear
        LiteralArgumentBuilder<CommandSourceStack> clearCommand = Commands.literal(CLEAR)
                .requires(source -> source.hasPermission(3));
        clearCommand.executes(MutekiCommand::executeClearAllMuteki);

        // muteki clear <players>
        clearCommand.then(Commands.argument(PLAYER, EntityArgument.players())
                .executes(MutekiCommand::executeClearPlayersMuteki));

        mutekiCommand.then(playerArgument);
        mutekiCommand.then(clearCommand);

        return mutekiCommand;
    }

    private static int executeClearAllMuteki(CommandContext<CommandSourceStack> context) {
        EffectManager.get().clearMuteki();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_all_muteki"), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeClearPlayersMuteki(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, PLAYER);
        if (players.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create(); // 与原版相同的提示
        }
        for (ServerPlayer player : players) {
            if (EffectManager.get().clearMuteki(player.getUUID())) {
                context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_muteki", player.getName()), false);
            } else {
                context.getSource().sendFailure(Component.translatable("battleroyale.message.player_not_muteki", player.getName()));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeAddMutekiDefaultTimeForPlayers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, PLAYER);
        if (players.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create(); // 与原版相同的提示
        }
        for (ServerPlayer player : players) {
            EffectManager.get().addMutekiPlayer(context.getSource().getLevel(), player, DEFAULT_TIME);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_muteki", player.getName()), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeAddMutekiWithTimeForPlayers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, PLAYER);
        int time = IntegerArgumentType.getInteger(context, TIME);
        if (players.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create(); // 与原版相同的提示
        }
        for (ServerPlayer player : players) {
            EffectManager.get().addMutekiPlayer(context.getSource().getLevel(), player, time);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_muteki", player.getName()), false);
        }
        return Command.SINGLE_SUCCESS;
    }
}