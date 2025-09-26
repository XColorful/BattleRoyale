package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager;

import javax.annotation.Nullable;

import static xiao.battleroyale.command.CommandArg.*;

public class ReloadCommand {
    
    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(RELOAD)
                .executes(ReloadCommand::reloadAllConfigs)
                .then(Commands.literal(LOOT)
                        .executes(context -> reloadLootConfigs(context, null))
                        .then(Commands.literal(LOOT_SPAWNER)
                                .executes(context -> reloadLootConfigs(context, LOOT_SPAWNER)))
                        .then(Commands.literal(ENTITY_SPAWNER)
                                .executes(context -> reloadLootConfigs(context, ENTITY_SPAWNER)))
                        .then(Commands.literal(AIRDROP)
                                .executes(context -> reloadLootConfigs(context, AIRDROP)))
                        .then(Commands.literal(AIRDROP_SPECIAL)
                                .executes(context -> reloadLootConfigs(context, AIRDROP_SPECIAL)))
                        .then(Commands.literal(SECRET_ROOM)
                                .executes(context -> reloadLootConfigs(context, SECRET_ROOM))))
                .then(Commands.literal(GAME)
                        .executes(context -> reloadGameConfigs(context, null))
                        .then(Commands.literal(ZONE)
                                .executes(context -> reloadGameConfigs(context, ZONE)))
                        .then(Commands.literal(SPAWN)
                                .executes(context -> reloadGameConfigs(context, SPAWN)))
                        .then(Commands.literal(GAMERULE)
                                .executes(context -> reloadGameConfigs(context, GAMERULE)))
                        .then(Commands.literal(BOT)
                                .executes(context -> reloadGameConfigs(context, BOT))))
                .then(Commands.literal(EFFECT)
                        .executes(context -> reloadEffectConfigs(context, null))
                        .then(Commands.literal(PARTICLE)
                                .executes(context -> reloadEffectConfigs(context, PARTICLE))))
                .then(Commands.literal(SERVER)
                        .executes(context -> reloadServerConfigs(context, null))
                        .then(Commands.literal(PERFORMANCE)
                                .executes(context -> reloadServerConfigs(context, PERFORMANCE)))
                        .then(Commands.literal(UTILITY)
                                .executes(context -> reloadServerConfigs(context, UTILITY))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getClient() {
        return Commands.literal(RELOAD)
                .then(Commands.literal(CLIENT)
                        .executes(context -> reloadClientConfigs(context, null))
                        .then(Commands.literal(RENDER)
                                .executes(context -> reloadClientConfigs(context, RENDER)))
                        .then(Commands.literal(DISPLAY)
                                .executes(context -> reloadClientConfigs(context, DISPLAY))));
    }

    public static int reloadAllConfigs(CommandContext<CommandSourceStack> context) {
        // 服务端配置
        LootConfigManager.get().reloadAllConfigs();
        GameConfigManager.get().reloadAllConfigs();
        EffectConfigManager.get().reloadAllConfigs();
        ServerConfigManager.get().reloadAllConfigs();

        // 客户端配置
        if (FMLEnvironment.dist.isClient()) {
            ClientConfigManager.get().reloadAllConfigs();
        } else {
            BattleRoyale.LOGGER.info("Skipped client config reload");
        }

        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.all_config_reloaded"), true);
        BattleRoyale.LOGGER.info("Reloaded all {} configs", BattleRoyale.MOD_ID);
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadLootConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            LootConfigManager.get().reloadAllConfigs();
            messageKey = "battleroyale.message.loot_config_reloaded";
        } else {
            int folderId = LootConfigTypeEnum.ALL_LOOT;
            switch (subType) {
                case LOOT_SPAWNER:
                    folderId = LootConfigTypeEnum.LOOT_SPAWNER;
                    messageKey = "battleroyale.message.loot_spawner_config_reloaded";
                    break;
                case ENTITY_SPAWNER:
                    folderId = LootConfigTypeEnum.ENTITY_SPAWNER;
                    messageKey = "battleroyale.message.entity_spawner_config_reloaded";
                    break;
                case AIRDROP:
                    folderId = LootConfigTypeEnum.AIRDROP;
                    messageKey = "battleroyale.message.airdrop_config_reloaded";
                    break;
                case AIRDROP_SPECIAL:
                    folderId = LootConfigTypeEnum.AIRDROP_SPECIAL;
                    messageKey = "battleroyale.message.airdrop_special_config_reloaded";
                    break;
                case SECRET_ROOM:
                    folderId = LootConfigTypeEnum.SECRET_ROOM;
                    messageKey = "battleroyale.message.secret_room_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_loot_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown loot sub-type for reload command: {}", subType);
                    return 0;
            }
            LootConfigManager.get().reloadConfigs(folderId);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Reloaded {} configs via command", subType != null ? subType : "all loot");
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadGameConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            GameConfigManager.get().reloadAllConfigs();
            messageKey = "battleroyale.message.game_config_reloaded";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case ZONE:
                    subManagerNameKey = ZoneConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.zone_config_reloaded";
                    break;
                case SPAWN:
                    subManagerNameKey = SpawnConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.spawn_config_reloaded";
                    break;
                case GAMERULE:
                    subManagerNameKey = GameruleConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.gamerule_config_reloaded";
                    break;
                case BOT:
                    subManagerNameKey = BotConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.bot_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown game sub-type for reload command: {}", subType);
                    return 0;
            }
            GameConfigManager.get().reloadConfigs(subManagerNameKey);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Reloaded {} configs via command", subType != null ? subType : "all game");
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadEffectConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            EffectConfigManager.get().reloadAllConfigs();
            messageKey = "battleroyale.message.effect_config_reloaded";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case PARTICLE:
                    subManagerNameKey = ParticleConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.particle_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown effect sub-type for reload command: {}", subType);
                    return 0;
            }
            EffectConfigManager.get().reloadConfigs(subManagerNameKey);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Reloaded {} effect configs via command", subType != null ? subType : "all effect");
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadClientConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            ClientConfigManager.get().reloadAllConfigs();
            messageKey = "battleroyale.message.client_config_reloaded";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case RENDER:
                    subManagerNameKey = RenderConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.render_config_reloaded";
                    break;
                case DISPLAY:
                    subManagerNameKey = DisplayConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.display_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_client_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown client sub-type for reload command: {}", subType);
                    return 0;
            }
            ClientConfigManager.get().reloadConfigs(subManagerNameKey);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Reloaded {} client configs via command", subType != null ? subType : "all client");
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadServerConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            ServerConfigManager.get().reloadAllConfigs();
            messageKey = "battleroyale.message.server_config_reloaded";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case PERFORMANCE:
                    subManagerNameKey = PerformanceConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.performance_config_reloaded";
                    break;
                case UTILITY:
                    subManagerNameKey = UtilityConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.utility_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_server_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown server sub-type for reload command: {}", subType);
                    return 0;
            }
            ServerConfigManager.get().reloadConfigs(subManagerNameKey);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Reloaded {} server configs via command", subType != null ? subType : "all server");
        return Command.SINGLE_SUCCESS;
    }
}