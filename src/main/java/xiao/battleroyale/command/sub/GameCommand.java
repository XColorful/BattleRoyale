package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.team.TeamManager;

import static xiao.battleroyale.command.CommandArg.*;
import static xiao.battleroyale.util.StringUtils.buildCommandString;

public class GameCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        // 不需要权限
        LiteralArgumentBuilder<CommandSourceStack> gameCommand = Commands.literal(GAME)
                .then(Commands.literal(LOBBY)
                        .executes(GameCommand::lobby))
                .then(Commands.literal(TO_LOBBY)
                        .executes(GameCommand::toLobby))
                .then(Commands.literal(SELECTED)
                        .executes(GameCommand::selectedConfigs))
                .then(Commands.literal(SPECTATE)
                        .requires(CommandSourceStack::isPlayer)
                        .executes(GameCommand::spectateGame));

        // 需要权限
        gameCommand.then(Commands.literal(LOAD)
                        .requires(source -> source.hasPermission(2))
                        .executes(GameCommand::loadGameConfig))
                .then(Commands.literal(INIT)
                        .requires(source -> source.hasPermission(2))
                        .executes(GameCommand::initGame))
                .then(Commands.literal(START)
                        .requires(source -> source.hasPermission(2))
                        .executes(GameCommand::startGame))
                .then(Commands.literal(STOP)
                        .requires(source -> source.hasPermission(2))
                        .executes(GameCommand::stopGame))
                .then(Commands.literal(OFFSET)
                        .requires(source -> source.hasPermission(2))
                        .executes(GameCommand::getGlobalOffset)
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .executes(GameCommand::globalOffset)));

        return gameCommand;
    }

    private static int loadGameConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel serverLevel = source.getLevel();
        GameManager gameManager = GameManager.get();

        if (gameManager.isInGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }

        gameManager.initGameConfig(serverLevel);
        if (gameManager.isPreparedForGame()) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.load_game_configs").withStyle(ChatFormatting.GREEN), false);
            BattleRoyale.LOGGER.info("Game config loaded via command.");
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.load_game_configs_failed").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Failed to load game config via command.");
            return 0;
        }
    }

    public static int initGame(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel serverLevel = source.getLevel();
        GameManager gameManager = GameManager.get();

        if (gameManager.isInGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }

        gameManager.initGame(serverLevel);
        if (gameManager.isReady()) { // 初始化完成，不包含队伍判断
            source.sendSuccess(() -> Component.translatable("battleroyale.message.game_init").withStyle(ChatFormatting.GREEN), false);
            BattleRoyale.LOGGER.info("Game initialized via command.");
            return Command.SINGLE_SUCCESS;
        } else if (!gameManager.isPreparedForGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_not_prepared").withStyle(ChatFormatting.YELLOW));
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.game_init_fail").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Failed to initialize game via command.");
        }
        return 0;
    }

    public static int startGame(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel serverLevel = source.getLevel();
        GameManager gameManager = GameManager.get();

        if (gameManager.isInGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_already_started").withStyle(ChatFormatting.RED));
            return 0;
        }

        if (gameManager.startGame(serverLevel)) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.game_start").withStyle(ChatFormatting.GREEN), false);
            BattleRoyale.LOGGER.info("Game started via command.");
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.game_start_failed").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Failed to start game via command.");
            return 0;
        }
    }

    public static int stopGame(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel serverLevel = source.getLevel();
        GameManager gameManager = GameManager.get();

        if (!gameManager.isInGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.no_game_in_progress").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        gameManager.stopGame(serverLevel);
        source.sendSuccess(() -> Component.translatable("battleroyale.message.game_stopped").withStyle(ChatFormatting.YELLOW), false);
        BattleRoyale.LOGGER.info("Game stopped via command.");
        return Command.SINGLE_SUCCESS;
    }

    public static int lobby(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) { // 向调用的玩家发送消息
            ServerPlayer player = context.getSource().getPlayerOrException();
            SpawnManager.get().sendLobbyInfo(player);
        } else { // 向全体玩家发送消息
            ServerLevel serverLevel = source.getLevel();
            SpawnManager.get().sendLobbyInfo(serverLevel);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int toLobby(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            ServerPlayer player = context.getSource().getPlayerOrException();
            TeamManager.get().teleportToLobby(player); // TeamManager 先处理游戏相关逻辑，再调用传送
            return Command.SINGLE_SUCCESS;
        } else {
            return 0;
        }
    }

    private static int globalOffset(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Vec3 offset = Vec3Argument.getVec3(context, XYZ);
        if (GameManager.get().setGlobalCenterOffset(offset)) {
            if (source.isPlayer()) {
                source.sendSuccess(() -> Component.translatable("battleroyale.message.set_global_offset", String.format("%.2f", offset.x), String.format("%.2f", offset.y), String.format("%.2f", offset.z)).withStyle(ChatFormatting.GREEN), false);
            }
            BattleRoyale.LOGGER.info("Set global center offset to {} via command", offset);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.game_in_progress"));
            return 0;
        }
    }
    private static int getGlobalOffset(CommandContext<CommandSourceStack> context) {
        Vec3 offset = GameManager.get().getGlobalCenterOffset();
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.check_global_offset", offset.x, offset.y, offset.z).withStyle(ChatFormatting.GREEN), false);
            ServerPlayer player = source.getPlayer();
            if (player != null) {
                BattleRoyale.LOGGER.info("{} check current global offset ({}) via command", player.getName().getString(), offset);
                return Command.SINGLE_SUCCESS;
            }
        }
        BattleRoyale.LOGGER.info("Check current global offset ({}) via command", offset);
        return Command.SINGLE_SUCCESS;
    }

    private static int selectedConfigs(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) { // 向调用的玩家发送消息
            ServerPlayer player = context.getSource().getPlayerOrException();
            GameManager.get().sendSelectedConfigsInfo(player);
        } else { // 向全体玩家发送消息
            ServerLevel serverLevel = source.getLevel();
            GameManager.get().sendSelectedConfigsInfo(serverLevel);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int spectateGame(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();

        if (GameManager.get().spectateGame(player)) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.switch_gamemode_success"), false);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.switch_gamemode_failed"));
            return 0;
        }
    }

    public static String toLobbyCommand() {
        return buildCommandString(
                MOD_ID,
                GAME,
                TO_LOBBY
        );
    }
}