package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;

public class ConfigCommand {

    private static final String CONFIG_NAME = "config";

    private static final String SPAWN_NAME = "spawn";
    private static final String GAMERULE_NAME = "gamerule";
    private static final String BOT_NAME = "bot";

    private static final String ID_NAME = "id";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(CONFIG_NAME)
                .then(Commands.literal(SPAWN_NAME)
                        .then(Commands.argument(ID_NAME, IntegerArgumentType.integer(0))
                                .executes(ConfigCommand::setSpawnConfigId)))
                .then(Commands.literal(GAMERULE_NAME)
                        .then(Commands.argument(ID_NAME, IntegerArgumentType.integer(0))
                                .executes(ConfigCommand::setGameruleConfigId)))
                .then(Commands.literal(BOT_NAME)
                        .then(Commands.argument(ID_NAME, IntegerArgumentType.integer(0))
                                .executes(ConfigCommand::setBotConfigId)));
    }

    private static int setSpawnConfigId(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID_NAME);
        if (GameManager.get().setSpawnConfigId(id)) {
            BattleRoyale.LOGGER.info("Set spawn config ID to {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.spawn_config_id_set", id), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_spawn_config_id", id));
            return 0;
        }
    }

    private static int setGameruleConfigId(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID_NAME);
        if (GameManager.get().setGameruleConfigId(id)) {
            BattleRoyale.LOGGER.info("Set gamerule config ID to {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.gamerule_config_id_set", id), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_gamerule_config_id", id));
            return 0;
        }
    }

    private static int setBotConfigId(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID_NAME);
        if (GameManager.get().setBotConfigId(id)) {
            BattleRoyale.LOGGER.info("Set bot config ID to {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.bot_config_id_set", id), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_bot_config_id", id));
            return 0; // Command failed
        }
    }
}