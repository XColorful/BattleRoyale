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
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.client.display.DisplayConfigManager;
import xiao.battleroyale.config.client.display.DisplayConfigManager.DisplayConfig;
import xiao.battleroyale.config.client.render.RenderConfigManager;
import xiao.battleroyale.config.client.render.RenderConfigManager.RenderConfig;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager.PerformanceConfig;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager.UtilityConfig;

import static xiao.battleroyale.command.CommandArg.*;

public class ConfigCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(CONFIG)
                .then(Commands.literal(LOOT)
                        .then(Commands.literal(LOOT_SPAWNER)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextLootSpawnerConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchLootSpawnerConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(ENTITY_SPAWNER)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextEntitySpawnerConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchEntitySpawnerConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(AIRDROP)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextAirdropConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchAirdropConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(AIRDROP_SPECIAL)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextAirdropSpecialConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchAirdropSpecialConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(SECRET_ROOM)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextSecretRoomConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchSecretRoomConfig)
                                        )
                                )
                        )
                )
                .then(Commands.literal(GAME)
                        .then(Commands.literal(BOT)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::setBotConfigId))
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextBotConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchBotConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(GAMERULE)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::setGameruleConfigId))
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextGameruleConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchGameruleConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(SPAWN)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::setSpawnConfigId)
                                )
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextSpawnConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchSpawnConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(ZONE)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextZoneConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchZoneConfig)
                                        )
                                )
                        )
                )
                .then(Commands.literal(EFFECT)
                        .then(Commands.literal(PARTICLE)
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextParticleConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchParticleConfig)
                                        )
                                )
                        )
                )
                .then(Commands.literal(SERVER)
                        .then(Commands.literal(PERFORMANCE)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::applyPerformanceConfig))
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextPerformanceConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchPerformanceConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(UTILITY)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::applyUtilityConfig))
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextUtilityConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchUtilityConfig)
                                        )
                                )
                        )
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getClient() {
        return Commands.literal(CONFIG)
                .then(Commands.literal(CLIENT)
                        .then(Commands.literal(RENDER)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::applyRenderConfig))
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextRenderConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchRenderConfig)
                                        )
                                )
                        )
                        .then(Commands.literal(DISPLAY)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(ConfigCommand::applyDisplayConfig))
                                .then(Commands.literal(SWITCH)
                                        .executes(ConfigCommand::switchNextDisplayConfig)
                                        .then(Commands.argument(FILE, StringArgumentType.string())
                                                .executes(ConfigCommand::switchDisplayConfig)
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
        String currentFileName = StringArgumentType.getString(context, FILE);
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
        String currentFileName = StringArgumentType.getString(context, FILE);
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
        String currentFileName = StringArgumentType.getString(context, FILE);
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
        String currentFileName = StringArgumentType.getString(context, FILE);
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
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (LootConfigManager.get().switchSecretRoomConfig(currentFileName)) {
            BattleRoyale.LOGGER.info("Switch secret room config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_secret_room_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_secret_room_config_file", currentFileName));
            return 0;
        }
    }

    private static int applyRenderConfig(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID);
        RenderConfig renderConfig = (RenderConfig) ClientConfigManager.get().getConfigEntry(RenderConfigManager.get().getNameKey(), id);
        if (renderConfig != null) {
            renderConfig.applyDefault();
            BattleRoyale.LOGGER.info("Applied render config {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.render_config_applied", id, renderConfig.getName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_render_config_id", id));
            return 0;
        }
    }
    private static int switchNextRenderConfig(CommandContext<CommandSourceStack> context) {
        if (ClientConfigManager.get().switchConfigFile(RenderConfigManager.get().getNameKey())) {
            String currentFileName = ClientConfigManager.get().getCurrentSelectedFileName(RenderConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch render config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_render_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_render_config_available"));
            return 0;
        }
    }
    private static int switchRenderConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (ClientConfigManager.get().switchConfigFile(RenderConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch render config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_render_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_render_config_file", currentFileName));
            return 0;
        }
    }
    private static int applyDisplayConfig(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID);
        DisplayConfig displayConfig = (DisplayConfig) ClientConfigManager.get().getConfigEntry(DisplayConfigManager.get().getNameKey(), id);
        if (displayConfig != null) {
            displayConfig.applyDefault();
            BattleRoyale.LOGGER.info("Applied display config {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.display_config_applied", id, displayConfig.getName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_display_config_id", id));
            return 0;
        }
    }
    private static int switchNextDisplayConfig(CommandContext<CommandSourceStack> context) {
        if (ClientConfigManager.get().switchConfigFile(DisplayConfigManager.get().getNameKey())) {
            String currentFileName = ClientConfigManager.get().getCurrentSelectedFileName(DisplayConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch display config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_display_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_display_config_available"));
            return 0;
        }
    }
    private static int switchDisplayConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (ClientConfigManager.get().switchConfigFile(DisplayConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch display config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_display_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_display_config_file", currentFileName));
            return 0;
        }
    }

    private static int applyPerformanceConfig(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID);
        PerformanceConfig performanceConfig = (PerformanceConfig) ServerConfigManager.get().getConfigEntry(PerformanceConfigManager.get().getNameKey(), id);
        if (performanceConfig != null) {
            performanceConfig.applyDefault();
            BattleRoyale.LOGGER.info("Applied performance config {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.performance_config_applied", id, performanceConfig.getName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_performance_config_id", id));
            return 0;
        }
    }
    private static int switchNextPerformanceConfig(CommandContext<CommandSourceStack> context) {
        if (ServerConfigManager.get().switchConfigFile(PerformanceConfigManager.get().getNameKey())) {
            String currentFileName = ServerConfigManager.get().getCurrentSelectedFileName(PerformanceConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch performance config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_performance_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_performance_config_available"));
            return 0;
        }
    }
    private static int switchPerformanceConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (ServerConfigManager.get().switchConfigFile(PerformanceConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch performance config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_performance_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_performance_config_file", currentFileName));
            return 0;
        }
    }
    private static int applyUtilityConfig(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID);
        UtilityConfig utilityConfig = (UtilityConfig) ServerConfigManager.get().getConfigEntry(UtilityConfigManager.get().getNameKey(), id);
        if (utilityConfig != null) {
            utilityConfig.applyDefault();
            BattleRoyale.LOGGER.info("Applied utility config {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.utility_config_applied", id, utilityConfig.getName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_utility_config_id", id));
            return 0;
        }
    }
    private static int switchNextUtilityConfig(CommandContext<CommandSourceStack> context) {
        if (ServerConfigManager.get().switchConfigFile(UtilityConfigManager.get().getNameKey())) {
            String currentFileName = ServerConfigManager.get().getCurrentSelectedFileName(UtilityConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch utility config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_utility_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_utility_config_available"));
            return 0;
        }
    }
    private static int switchUtilityConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (ServerConfigManager.get().switchConfigFile(UtilityConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch utility config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_utility_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_utility_config_file", currentFileName));
            return 0;
        }
    }

    private static int setBotConfigId(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID);
        if (GameManager.get().setBotConfigId(id)) {
            BattleRoyale.LOGGER.info("Set bot config ID to {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.bot_config_id_set", id, GameManager.get().getBotConfigName(id)), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_bot_config_id", id));
            return 0; // Command failed
        }
    }
    private static int switchNextBotConfig(CommandContext<CommandSourceStack> context) {
        if (GameConfigManager.get().switchConfigFile(BotConfigManager.get().getNameKey())) {
            String currentFileName = GameConfigManager.get().getCurrentSelectedFileName(BotConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch bot config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_bot_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_bot_config_available"));
            return 0;
        }
    }
    private static int switchBotConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (GameConfigManager.get().switchConfigFile(BotConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch bot config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_bot_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_bot_config_file", currentFileName));
            return 0;
        }
    }

    private static int setGameruleConfigId(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID);
        if (GameManager.get().setGameruleConfigId(id)) {
            BattleRoyale.LOGGER.info("Set gamerule config ID to {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.gamerule_config_id_set", id, GameManager.get().getGameruleConfigName(id)), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_gamerule_config_id", id));
            return 0;
        }
    }
    private static int switchNextGameruleConfig(CommandContext<CommandSourceStack> context) {
        if (GameConfigManager.get().switchConfigFile(GameruleConfigManager.get().getNameKey())) {
            String currentFileName = GameConfigManager.get().getCurrentSelectedFileName(GameruleConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch gamerule config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_gamerule_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_gamerule_config_available"));
            return 0;
        }
    }
    private static int switchGameruleConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (GameConfigManager.get().switchConfigFile(GameruleConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch gamerule config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_gamerule_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_gamerule_config_file", currentFileName));
            return 0;
        }
    }

    private static int setSpawnConfigId(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, ID);
        if (GameManager.get().setSpawnConfigId(id)) {
            BattleRoyale.LOGGER.info("Set spawn config ID to {} via command", id);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.spawn_config_id_set", id, GameManager.get().getSpawnConfigName(id)), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.invalid_spawn_config_id", id));
            return 0;
        }
    }
    private static int switchNextSpawnConfig(CommandContext<CommandSourceStack> context) {
        if (GameConfigManager.get().switchConfigFile(SpawnConfigManager.get().getNameKey())) {
            String currentFileName = GameConfigManager.get().getCurrentSelectedFileName(SpawnConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch spawn config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_spawn_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_spawn_config_available"));
            return 0;
        }
    }
    private static int switchSpawnConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (GameConfigManager.get().switchConfigFile(SpawnConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch spawn config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_spawn_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_spawn_config_file", currentFileName));
            return 0;
        }
    }

    private static int switchNextZoneConfig(CommandContext<CommandSourceStack> context) {
        if (GameConfigManager.get().switchConfigFile(ZoneConfigManager.get().getNameKey())) {
            String currentFileName = GameConfigManager.get().getCurrentSelectedFileName(ZoneConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch zone config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_zone_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_zone_config_available"));
            return 0;
        }
    }
    private static int switchZoneConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (GameConfigManager.get().switchConfigFile(ZoneConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch zone config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_zone_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_zone_config_file", currentFileName));
            return 0;
        }
    }

    private static int switchNextParticleConfig(CommandContext<CommandSourceStack> context) {
        if (EffectConfigManager.get().switchConfigFile(ParticleConfigManager.get().getNameKey())) {
            String currentFileName = EffectConfigManager.get().getCurrentSelectedFileName(ParticleConfigManager.get().getNameKey());
            BattleRoyale.LOGGER.info("Switch particle config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_particle_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_particle_config_available"));
            return 0;
        }
    }
    private static int switchParticleConfig(CommandContext<CommandSourceStack> context) {
        String currentFileName = StringArgumentType.getString(context, FILE);
        if (EffectConfigManager.get().switchConfigFile(ParticleConfigManager.get().getNameKey(), currentFileName)) {
            BattleRoyale.LOGGER.info("Switch particle config file to {} via command", currentFileName);
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.switch_particle_config_file", currentFileName), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_particle_config_file", currentFileName));
            return 0;
        }
    }
}