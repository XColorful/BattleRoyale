package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.client.display.DisplayConfigManager;
import xiao.battleroyale.config.client.render.RenderConfigManager;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager;

import javax.annotation.Nullable;

import static xiao.battleroyale.command.CommandArg.*;
import static xiao.battleroyale.command.sub.ConfigUtils.getConfigManager;
import static xiao.battleroyale.command.sub.ConfigUtils.getConfigSubManager;

public class ExampleCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(EXAMPLE)
                .executes(ExampleCommand::generateAllConfigs)
                .then(Commands.literal(LOOT)
                        .executes(context -> generateLootConfigs(context, null))
                        .then(Commands.literal(LOOT_SPAWNER)
                                .executes(context -> generateLootConfigs(context, LOOT_SPAWNER)))
                        .then(Commands.literal(ENTITY_SPAWNER)
                                .executes(context -> generateLootConfigs(context, ENTITY_SPAWNER)))
                        .then(Commands.literal(AIRDROP)
                                .executes(context -> generateLootConfigs(context, AIRDROP)))
                        .then(Commands.literal(AIRDROP_SPECIAL)
                                .executes(context -> generateLootConfigs(context, AIRDROP_SPECIAL)))
                        .then(Commands.literal(SECRET_ROOM)
                                .executes(context -> generateLootConfigs(context, SECRET_ROOM))))
                .then(Commands.literal(GAME)
                        .executes(context -> generateGameConfigs(context, null))
                        .then(Commands.literal(ZONE)
                                .executes(context -> generateGameConfigs(context, ZONE)))
                        .then(Commands.literal(SPAWN)
                                .executes(context -> generateGameConfigs(context, SPAWN)))
                        .then(Commands.literal(GAMERULE)
                                .executes(context -> generateGameConfigs(context, GAMERULE)))
                        .then(Commands.literal(BOT)
                                .executes(context -> generateGameConfigs(context, BOT))))
                .then(Commands.literal(EFFECT)
                        .executes(context -> generateEffectConfigs(context, null))
                        .then(Commands.literal(PARTICLE)
                                .executes(context -> generateEffectConfigs(context, PARTICLE))))
                .then(Commands.literal(SERVER)
                        .executes(context -> generateServerConfigs(context, null))
                        .then(Commands.literal(PERFORMANCE)
                                .executes(context -> generateServerConfigs(context, PERFORMANCE)))
                        .then(Commands.literal(UTILITY)
                                .executes(context -> generateServerConfigs(context, UTILITY))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getClient() {
        return Commands.literal(EXAMPLE)
                .then(Commands.literal(CLIENT)
                        .executes(context -> generateClientConfigs(context, null))
                        .then(Commands.literal(RENDER)
                                .executes(context -> generateClientConfigs(context, RENDER)))
                        .then(Commands.literal(DISPLAY)
                                .executes(context -> generateClientConfigs(context, DISPLAY))));
    }

    private static int generateAllConfigs(CommandContext<CommandSourceStack> context) {
        if (BattleRoyale.getModConfigManager().generateAllDefaultConfigs() > 0) {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.all_default_config_generated"), true);
            BattleRoyale.LOGGER.info("Generated all default {} configs", BattleRoyale.MOD_ID);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_config_manager_available", BattleRoyale.MOD_ID));
            return 0;
        }
    }

    private static int generateLootConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigSubManager<?> lootConfigManager = getConfigSubManager(context, LootConfigManager.get().getNameKey());
        if (lootConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = lootConfigManager.generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_loot_config_generated";
        } else {
            switch (subType) {
                case LOOT_SPAWNER:
                    success = lootConfigManager.generateDefaultConfigs(LootConfigTypeEnum.LOOT_SPAWNER) ? 1 : 0;
                    messageKey = "battleroyale.message.default_loot_spawner_config_generated";
                    break;
                case ENTITY_SPAWNER:
                    success = lootConfigManager.generateDefaultConfigs(LootConfigTypeEnum.ENTITY_SPAWNER) ? 1 : 0;
                    messageKey = "battleroyale.message.default_entity_spawner_config_generated";
                    break;
                case AIRDROP:
                    success = lootConfigManager.generateDefaultConfigs(LootConfigTypeEnum.AIRDROP) ? 1 : 0;
                    messageKey = "battleroyale.message.default_airdrop_config_generated";
                    break;
                case AIRDROP_SPECIAL:
                    success = lootConfigManager.generateDefaultConfigs(LootConfigTypeEnum.AIRDROP_SPECIAL) ? 1 : 0;
                    messageKey = "battleroyale.message.default_airdrop_special_config_generated";
                    break;
                case SECRET_ROOM:
                    success = lootConfigManager.generateDefaultConfigs(LootConfigTypeEnum.SECRET_ROOM) ? 1 : 0;
                    messageKey = "battleroyale.message.default_secret_room_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_loot_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown loot sub-type for generate command: {}", subType);
                    return 0;
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} configs via command", subType != null ? subType : "all loot");
        return Command.SINGLE_SUCCESS;
    }

    private static int generateGameConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigManager gameConfigManager = getConfigManager(context, GameConfigManager.get().getNameKey());
        if (gameConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = gameConfigManager.generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_game_config_generated";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case ZONE:
                    subManagerNameKey = ZoneConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_zone_config_generated";
                    break;
                case SPAWN:
                    subManagerNameKey = SpawnConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_spawn_config_generated";
                    break;
                case GAMERULE:
                    subManagerNameKey = GameruleConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_gamerule_config_generated";
                    break;
                case BOT:
                    subManagerNameKey = BotConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_bot_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown game sub-type for generate command: {}", subType);
                    return 0;
            }
            IConfigSubManager<?> configSubManager = getConfigSubManager(context, GameConfigManager.get().getNameKey(), subManagerNameKey);
            if (configSubManager == null) return 0;

            success = configSubManager.generateAllDefaultConfigs();
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} configs via command", subType != null ? subType : "all game");
        return Command.SINGLE_SUCCESS;
    }

    private static int generateEffectConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigManager effectConfigManager = getConfigManager(context, EffectConfigManager.get().getNameKey());
        if (effectConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = effectConfigManager.generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_effect_config_generated";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case PARTICLE:
                    subManagerNameKey = ParticleConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_particle_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_effect_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown effect sub-type for generate command: {}", subType);
                    return 0;
            }
            IConfigSubManager<?> configSubManager = effectConfigManager.getConfigSubManager(subManagerNameKey);
            if (configSubManager == null) return 0;

            success = configSubManager.generateAllDefaultConfigs();
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} effect configs via command", subType != null ? subType : "all effect");
        return Command.SINGLE_SUCCESS;
    }

    private static int generateClientConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigManager clientConfigManager = getConfigManager(context, ClientConfigManager.get().getNameKey());
        if (clientConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = clientConfigManager.generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_client_config_generated";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case RENDER:
                    subManagerNameKey = RenderConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_render_config_generated";
                    break;
                case DISPLAY:
                    subManagerNameKey = DisplayConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_display_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_client_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown client sub-type for generate command: {}", subType);
                    return 0;
            }
            IConfigSubManager<?> configSubManager = clientConfigManager.getConfigSubManager(subManagerNameKey);
            if (configSubManager == null) return 0;

            success = configSubManager.generateAllDefaultConfigs();
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} client configs via command", subType != null ? subType : "all client");
        return Command.SINGLE_SUCCESS;
    }

    private static int generateServerConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigManager serverConfigManager = getConfigManager(context, ServerConfigManager.get().getNameKey());
        if (serverConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = serverConfigManager.generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_server_config_generated";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case PERFORMANCE:
                    subManagerNameKey = PerformanceConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_performance_config_generated";
                    break;
                case UTILITY:
                    subManagerNameKey = UtilityConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.default_utility_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_server_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown server sub-type for generate command: {}", subType);
                    return 0;
            }
            IConfigSubManager<?> configSubManager = serverConfigManager.getConfigSubManager(subManagerNameKey);
            if (configSubManager == null) return 0;

            success = configSubManager.generateAllDefaultConfigs();
        }

        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} server configs via command", subType != null ? subType : "all server");
        return Command.SINGLE_SUCCESS;
    }
}