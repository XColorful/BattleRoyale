package xiao.battleroyale.compat.pubgmc;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.command.sub.GameCommand;
import xiao.battleroyale.command.sub.LootCommand;
import xiao.battleroyale.command.sub.ReloadCommand;
import xiao.battleroyale.command.sub.TeamCommand;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;

import static xiao.battleroyale.compat.pubgmc.CommandArg.*;

public class PubgmcCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(getGame());
        dispatcher.register(getLoot());
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getGame() {
        return Commands.literal(GAME)
                .then(Commands.literal(LOBBY)
                        .executes(PubgmcCommand::lobbyInfo))
                .then(Commands.literal(LEAVE)
                        .executes(PubgmcCommand::leaveGame))
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal(INIT)
                        .executes(PubgmcCommand::initGame))
                .then(Commands.literal(START)
                        .executes(PubgmcCommand::startGameWithoutMap)
                        .then(Commands.argument(MAP_NAME, StringArgumentType.string())
                                .executes(PubgmcCommand::startGameWithMap)))
                .then(Commands.literal(LOBBY)
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .then(Commands.argument(RADIUS, DoubleArgumentType.doubleArg())
                                        .executes(PubgmcCommand::setLobbyWithCoordsAndRadius))))
                .then(Commands.literal(SELECT)
                        .then(Commands.literal(PUBGMC_BATTLEROYALE)
                                .executes(PubgmcCommand::selectBattleRoyaleMode)))
                .then(Commands.literal(STOP)
                        .executes(PubgmcCommand::stopGame))
                .then(Commands.literal(RELOAD_CONFIGS)
                        .executes(PubgmcCommand::reloadConfigs))
                .then(Commands.literal(MAP)
                        .then(Commands.literal(CREATE)
                                .then(Commands.argument(MAP_NAME, StringArgumentType.string())
                                        .then(Commands.literal(CENTER)
                                                .then(Commands.argument(XZ, Vec2Argument.vec2())
                                                        .then(Commands.argument(SIDE, DoubleArgumentType.doubleArg())
                                                                .executes(PubgmcCommand::createMap))))))
                        .then(Commands.argument(MAP_NAME, StringArgumentType.string())
                                .then(Commands.literal(DELETE)
                                        .executes(PubgmcCommand::deleteMap))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getLoot() {
        return Commands.literal(GENERATOR)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal(GENERATE)
                        .executes(PubgmcCommand::generatorGenerate));
    }

    private static int initGame(CommandContext<CommandSourceStack> context) {
        return GameCommand.initGame(context);
    }

    private static int startGameWithoutMap(CommandContext<CommandSourceStack> context) {
        return GameCommand.startGame(context);
    }

    private static int startGameWithMap(CommandContext<CommandSourceStack> context) {
        return GameCommand.startGame(context);
    }

    private static int setLobbyWithCoordsAndRadius(CommandContext<CommandSourceStack> context) {
        Vec3 coords = Vec3Argument.getVec3(context, XYZ);
        double radius = DoubleArgumentType.getDouble(context, RADIUS);
        if (SpawnManager.get().setPubgmcLobby(coords, radius)) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.pubgmc.game.lobby.created"), false);
            BattleRoyale.LOGGER.info("Created lobby at {} (radius:{}) via pubgmc command", coords, radius);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("commands.pubgmc.game.lobby.not_created"));
            BattleRoyale.LOGGER.info("Failed to create pubgmc lobby");
            return 0;
        }
    }

    private static int lobbyInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return GameCommand.lobby(context);
    }

    private static int selectBattleRoyaleMode(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.translatable("commands.pubgmc.game.type_selected", BattleRoyale.MOD_ID), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int stopGame(CommandContext<CommandSourceStack> context) {
        return GameCommand.stopGame(context);
    }

    private static int reloadConfigs(CommandContext<CommandSourceStack> context) {
        return ReloadCommand.reloadAllConfigs(context);
    }

    private static int createMap(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String mapName = StringArgumentType.getString(context, MAP_NAME);
        Vec2 xzCoords = Vec2Argument.getVec2(context, XZ);
        Vec3 offset = new Vec3(xzCoords.x, 0, xzCoords.y);
        double side = DoubleArgumentType.getDouble(context, SIDE);
        if (GameManager.get().setGlobalCenterOffset(offset)) {
            if (source.isPlayer()) {
                source.sendSuccess(() -> Component.translatable("battleroyale.message.set_global_offset", String.format("%.2f", offset.x), String.format("%.2f", offset.y), String.format("%.2f", offset.z)).withStyle(ChatFormatting.GREEN), false);
            }
            BattleRoyale.LOGGER.info("Set global center offset to {} via command", offset);
            source.sendSuccess(() -> Component.translatable("commands.pubgmc.game.map.created", mapName), false);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.game_in_progress"));
            return 0;
        }
    }

    private static int deleteMap(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Vec3 offset = Vec3.ZERO;
        if (GameManager.get().setGlobalCenterOffset(offset)) {
            if (source.isPlayer()) {
                source.sendSuccess(() -> Component.translatable("battleroyale.message.set_global_offset", String.format("%.2f", offset.x), String.format("%.2f", offset.y), String.format("%.2f", offset.z)).withStyle(ChatFormatting.GREEN), false);
            }
            BattleRoyale.LOGGER.info("Set global center offset to {} via command", offset);
            source.sendSuccess(() -> Component.translatable("commands.pubgmc.game.map.deleted"), false);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.game_in_progress"));
            return 0;
        }
    }

    private static int generatorGenerate(CommandContext<CommandSourceStack> context) {
        return LootCommand.generateAllLoadedLoot(context);
    }

    private static int leaveGame(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return TeamCommand.leaveTeam(context);
    }
}
