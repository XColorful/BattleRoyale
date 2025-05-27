package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.ChatFormatting;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;

public class GameCommand {

    private static final String GAME_NAME = "game";

    private static final String LOAD_NAME = "load";
    private static final String INIT_NAME = "init";
    private static final String START_NAME = "start";
    private static final String STOP_NAME = "stop";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(GAME_NAME)
                .then(Commands.literal(LOAD_NAME)
                        .executes(GameCommand::loadGameConfig))
                .then(Commands.literal(INIT_NAME)
                        .executes(GameCommand::initGame))
                .then(Commands.literal(START_NAME)
                        .executes(GameCommand::startGame))
                .then(Commands.literal(STOP_NAME)
                        .executes(GameCommand::stopGame));
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

    private static int initGame(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel serverLevel = source.getLevel();
        GameManager gameManager = GameManager.get();

        if (gameManager.isInGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }

        if (!gameManager.isPreparedForGame()) {
            source.sendFailure(Component.translatable("battleroyale.message.game_not_prepared").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        gameManager.initGame(serverLevel);
        if (gameManager.isReady()) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.game_init").withStyle(ChatFormatting.GREEN), false);
            BattleRoyale.LOGGER.info("Game initialized via command.");
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.game_init_failed").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Failed to initialize game via command.");
            return 0;
        }
    }

    private static int startGame(CommandContext<CommandSourceStack> context) {
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

    private static int stopGame(CommandContext<CommandSourceStack> context) {
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
}