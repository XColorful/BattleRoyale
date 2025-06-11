package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum; // 导入 LootConfigTypeEnum

public class ConfigCommand {

    private static final String CONFIG_NAME = "config";

    private static final String LOOT_NAME = "loot";
    private static final String GAME_NAME = "game";

    // Loot 子类配置
    private static final String LOOT_SPAWNER_NAME = "loot_spawner";
    private static final String ENTITY_SPAWNER_NAME = "entity_spawner";
    private static final String AIRDROP_NAME = "airdrop";
    private static final String AIRDROP_SPECIAL_NAME = "airdrop_special";
    private static final String SECRET_ROOM_NAME = "secret_room";

    // Game 子类配置
    private static final String BOT_NAME = "bot";
    private static final String GAMERULE_NAME = "gamerule";
    private static final String SPAWN_NAME = "spawn";
    private static final String ZONE_NAME = "zone";

    private static final String ID_NAME = "id";
    private static final String SWITCH = "switch";
    private static final String FILE_NAME = "fileName";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(CONFIG_NAME)
                .then(Commands.literal(LOOT_NAME)
                        .then(Commands.literal(LOOT_SPAWNER_NAME)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextLootSpawnerConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchLootSpawnerConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(ENTITY_SPAWNER_NAME)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextEntitySpawnerConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchEntitySpawnerConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(AIRDROP_NAME)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextAirdropConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchAirdropConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(AIRDROP_SPECIAL_NAME)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextAirdropSpecialConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchAirdropSpecialConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(SECRET_ROOM_NAME)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextSecretRoomConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchSecretRoomConfig)
                                        )
                                )
                        )
                )
                .then(Commands.literal(GAME_NAME)
                        .then(Commands.literal(BOT_NAME)
                                .then(Commands.argument(ID_NAME, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::setBotConfigId))
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextBotConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchBotConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(GAMERULE_NAME)
                                .then(Commands.argument(ID_NAME, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::setGameruleConfigId))
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextGameruleConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchGameruleConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(SPAWN_NAME)
                                .then(Commands.argument(ID_NAME, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::setSpawnConfigId)
                                )
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextSpawnConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchSpawnConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(ZONE_NAME)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextZoneConfig)
                                        .then(Commands.argument(FILE_NAME, StringArgumentType.string())
                                                .executes(ConfigCommand::switchZoneConfig)
                                        )
                                )
                        )
                );
    }

    private static int switchNextLootSpawnerConfig(CommandContext<CommandSourceStack> context) {
        if (LootConfigManager.get().switchNextLootSpawnerConfig()) {
            String currentFileName = LootConfigManager.get().getLootSpawnerConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch loot spawner config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_loot_spawner_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_loot_spawner_config_available"));
            return 0;
        }
    }
    private static int switchLootSpawnerConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (LootConfigManager.get().switchLootSpawnerConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch loot spawner config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_loot_spawner_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_loot_spawner_config_file", currentFileName));
            return 0;
        }
    }
    private static int switchNextEntitySpawnerConfig(CommandContext<CommandSourceStack> context) {
        if (LootConfigManager.get().switchNextEntitySpawnerConfig()) {
            String currentFileName = LootConfigManager.get().getEntitySpawnerConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch entity spawner config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_entity_spawner_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_entity_spawner_config_available"));
            return 0;
        }
    }
    private static int switchEntitySpawnerConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (LootConfigManager.get().switchEntitySpawnerConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch entity spawner config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_entity_spawner_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_entity_spawner_config_file", currentFileName));
            return 0;
        }
    }
    private static int switchNextAirdropConfig(CommandContext<CommandSourceStack> context) {
        if (LootConfigManager.get().switchNextAirdropConfig()) {
            String currentFileName = LootConfigManager.get().getAirdropConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch airdrop config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_airdrop_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_airdrop_config_available"));
            return 0;
        }
    }
    private static int switchAirdropConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (LootConfigManager.get().switchAirdropConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch airdrop config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_airdrop_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_airdrop_config_file", currentFileName));
            return 0;
        }
    }
    private static int switchNextAirdropSpecialConfig(CommandContext<CommandSourceStack> context) {
        if (LootConfigManager.get().switchNextAirdropSpecialConfig()) {
            String currentFileName = LootConfigManager.get().getAirdropSpecialConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch airdrop special config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_airdrop_special_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_airdrop_special_config_available"));
            return 0;
        }
    }
    private static int switchAirdropSpecialConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (LootConfigManager.get().switchAirdropSpecialConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch airdrop special config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_airdrop_special_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_airdrop_special_config_file", currentFileName));
            return 0;
        }
    }
    private static int switchNextSecretRoomConfig(CommandContext<CommandSourceStack> context) {
        if (LootConfigManager.get().switchNextSecretRoomConfig()) {
            String currentFileName = LootConfigManager.get().getSecretRoomConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch secret room config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_secret_room_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_secret_room_config_available"));
            return 0;
        }
    }
    private static int switchSecretRoomConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (LootConfigManager.get().switchSecretRoomConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch secret room config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_secret_room_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_secret_room_config_file", currentFileName));
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
    private static int switchNextBotConfig(CommandContext<CommandSourceStack> context) {
        if (GameConfigManager.get().switchNextBotConfig()) {
            String currentFileName = GameConfigManager.get().getBotConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch bot config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_bot_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_bot_config_available"));
            return 0;
        }
    }
    private static int switchBotConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (GameConfigManager.get().switchBotConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch bot config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_bot_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_bot_config_file", currentFileName));
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
    private static int switchNextGameruleConfig(CommandContext<CommandSourceStack> context) {
        if (GameConfigManager.get().switchNextGameruleConfig()) {
            String currentFileName = GameConfigManager.get().getGameruleConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch gamerule config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_gamerule_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_gamerule_config_available"));
            return 0;
        }
    }
    private static int switchGameruleConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (GameConfigManager.get().switchGameruleConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch gamerule config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_gamerule_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_gamerule_config_file", currentFileName));
            return 0;
        }
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
    private static int switchNextSpawnConfig(CommandContext<CommandSourceStack> context) {
        if (GameConfigManager.get().switchNextSpawnConfig()) {
            String currentFileName = GameConfigManager.get().getSpawnConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch spawn config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_spawn_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_spawn_config_available"));
            return 0;
        }
    }
    private static int switchSpawnConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (GameConfigManager.get().switchSpawnConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch spawn config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_spawn_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_spawn_config_file", currentFileName));
            return 0;
        }
    }

    private static int switchNextZoneConfig(CommandContext<CommandSourceStack> context) {
        if (GameConfigManager.get().switchNextZoneConfig()) {
            String currentFileName = GameConfigManager.get().getZoneConfigEntryFileName();
            BattleRoyale.LOGGER.info("Switch zone config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_zone_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_zone_config_available"));
            return 0;
        }
    }
    private static int switchZoneConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE_NAME);
        if (GameConfigManager.get().switchZoneConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch zone config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_zone_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_zone_config_file", currentFileName));
            return 0;
        }
    }
}