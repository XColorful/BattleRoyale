package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
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

public class BackupCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(BACKUP)
                .executes(BackupCommand::backupAllConfigs)
                .then(Commands.literal(LOOT)
                        .executes(context -> backupLootConfigs(context, null))
                        .then(Commands.literal(LOOT_SPAWNER)
                                .executes(context -> backupLootConfigs(context, LOOT_SPAWNER)))
                        .then(Commands.literal(ENTITY_SPAWNER)
                                .executes(context -> backupLootConfigs(context, ENTITY_SPAWNER)))
                        .then(Commands.literal(AIRDROP)
                                .executes(context -> backupLootConfigs(context, AIRDROP)))
                        .then(Commands.literal(AIRDROP_SPECIAL)
                                .executes(context -> backupLootConfigs(context, AIRDROP_SPECIAL)))
                        .then(Commands.literal(SECRET_ROOM)
                                .executes(context -> backupLootConfigs(context, SECRET_ROOM))))
                .then(Commands.literal(GAME)
                        .executes(context -> backupGameConfigs(context, null))
                        .then(Commands.literal(ZONE)
                                .executes(context -> backupGameConfigs(context, ZONE)))
                        .then(Commands.literal(SPAWN)
                                .executes(context -> backupGameConfigs(context, SPAWN)))
                        .then(Commands.literal(GAMERULE)
                                .executes(context -> backupGameConfigs(context, GAMERULE)))
                        .then(Commands.literal(BOT)
                                .executes(context -> backupGameConfigs(context, BOT))))
                .then(Commands.literal(EFFECT)
                        .executes(context -> backupEffectConfigs(context, null))
                        .then(Commands.literal(PARTICLE)
                                .executes(context -> backupEffectConfigs(context, PARTICLE))))
                .then(Commands.literal(SERVER)
                        .executes(context -> backupServerConfigs(context, null))
                        .then(Commands.literal(PERFORMANCE)
                                .executes(context -> backupServerConfigs(context, PERFORMANCE)))
                        .then(Commands.literal(UTILITY)
                                .executes(context -> backupServerConfigs(context, UTILITY))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getClient() {
        return Commands.literal(BACKUP)
                .then(Commands.literal(CLIENT)
                        .executes(context -> backupClientConfigs(context, null))
                        .then(Commands.literal(RENDER)
                                .executes(context -> backupClientConfigs(context, RENDER)))
                        .then(Commands.literal(DISPLAY)
                                .executes(context -> backupClientConfigs(context, DISPLAY))));
    }

    private static int backupAllConfigs(CommandContext<CommandSourceStack> context) {
        BattleRoyale.getModConfigManager().backupAllConfigs();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.all_config_backed_up"), true);
        BattleRoyale.LOGGER.info("Backed up all {} configs", BattleRoyale.MOD_ID);
        return Command.SINGLE_SUCCESS;
    }

    private static int backupLootConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            LootConfigManager.get().backupAllConfigs();
            messageKey = "battleroyale.message.loot_config_backed_up";
        } else {
            int folderId = LootConfigTypeEnum.ALL_LOOT;
            switch (subType) {
                case LOOT_SPAWNER:
                    folderId = LootConfigTypeEnum.LOOT_SPAWNER;
                    messageKey = "battleroyale.message.loot_spawner_config_backed_up";
                    break;
                case ENTITY_SPAWNER:
                    folderId = LootConfigTypeEnum.ENTITY_SPAWNER;
                    messageKey = "battleroyale.message.entity_spawner_config_backed_up";
                    break;
                case AIRDROP:
                    folderId = LootConfigTypeEnum.AIRDROP;
                    messageKey = "battleroyale.message.airdrop_config_backed_up";
                    break;
                case AIRDROP_SPECIAL:
                    folderId = LootConfigTypeEnum.AIRDROP_SPECIAL;
                    messageKey = "battleroyale.message.airdrop_special_config_backed_up";
                    break;
                case SECRET_ROOM:
                    folderId = LootConfigTypeEnum.SECRET_ROOM;
                    messageKey = "battleroyale.message.secret_room_config_backed_up";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_loot_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown loot sub-type for backup command: {}", subType);
                    return 0;
            }
            LootConfigManager.get().backupConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot(), folderId);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Backed up {} configs via command", subType != null ? subType : "all loot");
        return Command.SINGLE_SUCCESS;
    }

    private static int backupGameConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            GameConfigManager.get().backupAllConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot());
            messageKey = "battleroyale.message.game_config_backed_up";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case ZONE:
                    subManagerNameKey = ZoneConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.zone_config_backed_up";
                    break;
                case SPAWN:
                    subManagerNameKey = SpawnConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.spawn_config_backed_up";
                    break;
                case GAMERULE:
                    subManagerNameKey = GameruleConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.gamerule_config_backed_up";
                    break;
                case BOT:
                    subManagerNameKey = BotConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.bot_config_backed_up";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown game sub-type for backup command: {}", subType);
                    return 0;
            }
            GameConfigManager.get().backupConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot(), subManagerNameKey);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Backed up {} configs via command", subType != null ? subType : "all game");
        return Command.SINGLE_SUCCESS;
    }

    private static int backupEffectConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            EffectConfigManager.get().backupAllConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot());
            messageKey = "battleroyale.message.effect_config_backed_up";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case PARTICLE:
                    subManagerNameKey = ParticleConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.particle_config_backed_up";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_effect_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown effect sub-type for backup command: {}", subType);
                    return 0;
            }
            EffectConfigManager.get().backupConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot(), subManagerNameKey);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Backed up {} effect configs via command", subType != null ? subType : "all effect");
        return Command.SINGLE_SUCCESS;
    }

    private static int backupClientConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            ClientConfigManager.get().backupAllConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot());
            messageKey = "battleroyale.message.client_config_backed_up";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case RENDER:
                    subManagerNameKey = RenderConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.render_config_backed_up";
                    break;
                case DISPLAY:
                    subManagerNameKey = DisplayConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.display_config_backed_up";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_client_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown client sub-type for backup command: {}", subType);
                    return 0;
            }
            ClientConfigManager.get().backupConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot(), subManagerNameKey);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Backed up {} client configs via command", subType != null ? subType : "all client");
        return Command.SINGLE_SUCCESS;
    }

    private static int backupServerConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            ServerConfigManager.get().backupAllConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot());
            messageKey = "battleroyale.message.server_config_backed_up";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case PERFORMANCE:
                    subManagerNameKey = PerformanceConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.performance_config_backed_up";
                    break;
                case UTILITY:
                    subManagerNameKey = UtilityConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.utility_config_backed_up";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_server_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown server sub-type for backup command: {}", subType);
                    return 0;
            }
            ServerConfigManager.get().backupConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot(), subManagerNameKey);
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Backed up {} server configs via command", subType != null ? subType : "all server");
        return Command.SINGLE_SUCCESS;
    }
}