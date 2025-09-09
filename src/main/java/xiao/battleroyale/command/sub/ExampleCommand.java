package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.config.common.server.ServerConfigManager;

import javax.annotation.Nullable;

import static xiao.battleroyale.command.CommandArg.*;

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
        LootConfigManager.get().generateDefaultConfigs(LootConfigTypeEnum.ALL_LOOT);
        GameConfigManager.get().generateAllDefaultConfigs();
        EffectConfigManager.get().generateAllDefaultConfigs();
        ClientConfigManager.get().generateAllDefaultConfigs();

        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.all_default_config_generated"), true);
        BattleRoyale.LOGGER.info("Generated all default {} configs", BattleRoyale.MOD_ID);
        return Command.SINGLE_SUCCESS;
    }

    private static int generateLootConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            LootConfigManager.get().generateDefaultConfigs(LootConfigTypeEnum.ALL_LOOT);
            messageKey = "battleroyale.message.default_loot_config_generated";
        } else {
            switch (subType) {
                case LOOT_SPAWNER:
                    LootConfigManager.get().generateDefaultConfigs(LootConfigTypeEnum.LOOT_SPAWNER);
                    messageKey = "battleroyale.message.default_loot_spawner_config_generated";
                    break;
                case ENTITY_SPAWNER:
                    LootConfigManager.get().generateDefaultConfigs(LootConfigTypeEnum.ENTITY_SPAWNER);
                    messageKey = "battleroyale.message.default_entity_spawner_config_generated";
                    break;
                case AIRDROP:
                    LootConfigManager.get().generateDefaultConfigs(LootConfigTypeEnum.AIRDROP);
                    messageKey = "battleroyale.message.default_airdrop_config_generated";
                    break;
                case AIRDROP_SPECIAL:
                    LootConfigManager.get().generateDefaultConfigs(LootConfigTypeEnum.AIRDROP_SPECIAL);
                    messageKey = "battleroyale.message.default_airdrop_special_config_generated";
                    break;
                case SECRET_ROOM:
                    LootConfigManager.get().generateDefaultConfigs(LootConfigTypeEnum.SECRET_ROOM);
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
        String messageKey;
        if (subType == null) {
            GameConfigManager.get().generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_game_config_generated";
        } else {
            switch (subType) {
                case ZONE:
                    GameConfigManager.get().generateDefaultZoneConfigs();
                    messageKey = "battleroyale.message.default_zone_config_generated";
                    break;
                case SPAWN:
                    GameConfigManager.get().generateDefaultSpawnConfigs();
                    messageKey = "battleroyale.message.default_spawn_config_generated";
                    break;
                case GAMERULE:
                    GameConfigManager.get().generateDefaultGameruleConfigs();
                    messageKey = "battleroyale.message.default_gamerule_config_generated";
                    break;
                case BOT:
                    GameConfigManager.get().generateDefaultBotConfigs();
                    messageKey = "battleroyale.message.default_bot_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown game sub-type for generate command: {}", subType);
                    return 0;
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} configs via command", subType != null ? subType : "all game");
        return Command.SINGLE_SUCCESS;
    }

    private static int generateEffectConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            EffectConfigManager.get().generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_effect_config_generated";
        } else {
            switch (subType) {
                case PARTICLE:
                    EffectConfigManager.get().generateDefaultParticleConfigs();
                    messageKey = "battleroyale.message.default_particle_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_effect_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown effect sub-type for generate command: {}", subType);
                    return 0;
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} effect configs via command", subType != null ? subType : "all effect");
        return Command.SINGLE_SUCCESS;
    }

    private static int generateClientConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            ClientConfigManager.get().generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_client_config_generated";
        } else {
            switch (subType) {
                case RENDER:
                    ClientConfigManager.get().generateDefaultRenderConfigs();
                    messageKey = "battleroyale.message.default_render_config_generated";
                    break;
                case DISPLAY:
                    ClientConfigManager.get().generateDefaultDisplayConfigs();
                    messageKey = "battleroyale.message.default_display_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_client_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown client sub-type for generate command: {}", subType);
                    return 0;
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} client configs via command", subType != null ? subType : "all client");
        return Command.SINGLE_SUCCESS;
    }

    private static int generateServerConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            ServerConfigManager.get().generateAllDefaultConfigs();
            messageKey = "battleroyale.message.default_server_config_generated";
        } else {
            switch (subType) {
                case PERFORMANCE:
                    ServerConfigManager.get().generateDefaultPerformanceConfigs();
                    messageKey = "battleroyale.message.default_performance_config_generated";
                    break;
                case UTILITY:
                    ServerConfigManager.get().generateDefaultUtilityConfigs();
                    messageKey = "battleroyale.message.default_utility_config_generated";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_server_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown server sub-type for generate command: {}", subType);
                    return 0;
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Generated {} server configs via command", subType != null ? subType : "all server");
        return Command.SINGLE_SUCCESS;
    }
}