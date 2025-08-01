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
import xiao.battleroyale.common.effect.boost.BoostData;

import java.util.Collection;

import static xiao.battleroyale.command.CommandArg.*;

public class BoostCommand {

    private static final int DEFAULT_BOOST = BoostData.getBoostLimit() / 5;

    public static LiteralArgumentBuilder<CommandSourceStack> get() {

        LiteralArgumentBuilder<CommandSourceStack> boostCommand = Commands.literal(BOOST);

        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> playerBase = Commands.argument(PLAYER, EntityArgument.players());
        RequiredArgumentBuilder<CommandSourceStack, Integer> amountArg = Commands.argument(AMOUNT, IntegerArgumentType.integer(1, BoostData.getBoostLimit()));

        // boost <players>
        playerBase.executes(BoostCommand::addBoostForPlayersDefault);
        // boost <players> <amount>
        amountArg.executes(BoostCommand::addBoostForPlayersWithAmount);
        playerBase.then(amountArg);

        boostCommand.then(playerBase);

        // boost clear [players] / boost clear
        LiteralArgumentBuilder<CommandSourceStack> clearCommand = Commands.literal(CLEAR)
                .requires(source -> source.hasPermission(3));

        // boost clear=
        clearCommand.executes(BoostCommand::clearAllBoost);
        // boost clear <players>
        clearCommand.then(Commands.argument(PLAYER, EntityArgument.players())
                .executes(BoostCommand::clearPlayersBoost));

        boostCommand.then(clearCommand);

        return boostCommand;
    }

    private static int addBoostForPlayersDefault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, PLAYER);
        if (players.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        for (ServerPlayer player : players) {
            EffectManager.get().addBoost(player.getUUID(), DEFAULT_BOOST, context.getSource().getLevel());
            int hundredInt = (int) (BoostData.getBoostPercentage(DEFAULT_BOOST) * 100);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_boost", player.getName(), hundredInt), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addBoostForPlayersWithAmount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, PLAYER);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        if (players.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        for (ServerPlayer player : players) {
            EffectManager.get().addBoost(player.getUUID(), amount, context.getSource().getLevel());
            int hundredInt = (int) (BoostData.getBoostPercentage(amount) * 100);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_boost", player.getName(), hundredInt), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clearAllBoost(CommandContext<CommandSourceStack> context) {
        EffectManager.get().clearBoost();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_all_boost"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int clearPlayersBoost(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, PLAYER);
        if (players.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        for (ServerPlayer player : players) {
            EffectManager.get().clearBoost(player.getUUID());
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_player_boost", player.getName()), false);
        }
        return Command.SINGLE_SUCCESS;
    }
}
