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

public class SaveCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(SAVE)
                .executes(SaveCommand::saveAllConfigs)
                .then(Commands.literal(LOOT)
                        .executes(context -> saveLootConfigs(context, null))
                        .then(Commands.literal(LOOT_SPAWNER)
                                .executes(context -> saveLootConfigs(context, LOOT_SPAWNER)))
                        .then(Commands.literal(ENTITY_SPAWNER)
                                .executes(context -> saveLootConfigs(context, ENTITY_SPAWNER)))
                        .then(Commands.literal(AIRDROP)
                                .executes(context -> saveLootConfigs(context, AIRDROP)))
                        .then(Commands.literal(AIRDROP_SPECIAL)
                                .executes(context -> saveLootConfigs(context, AIRDROP_SPECIAL)))
                        .then(Commands.literal(SECRET_ROOM)
                                .executes(context -> saveLootConfigs(context, SECRET_ROOM))))
                .then(Commands.literal(GAME)
                        .executes(context -> saveGameConfigs(context, null))
                        .then(Commands.literal(ZONE)
                                .executes(context -> saveGameConfigs(context, ZONE)))
                        .then(Commands.literal(SPAWN)
                                .executes(context -> saveGameConfigs(context, SPAWN)))
                        .then(Commands.literal(GAMERULE)
                                .executes(context -> saveGameConfigs(context, GAMERULE)))
                        .then(Commands.literal(BOT)
                                .executes(context -> saveGameConfigs(context, BOT))))
                .then(Commands.literal(EFFECT)
                        .executes(context -> saveEffectConfigs(context, null))
                        .then(Commands.literal(PARTICLE)
                                .executes(context -> saveEffectConfigs(context, PARTICLE))))
                .then(Commands.literal(SERVER)
                        .executes(context -> saveServerConfigs(context, null))
                        .then(Commands.literal(PERFORMANCE)
                                .executes(context -> saveServerConfigs(context, PERFORMANCE)))
                        .then(Commands.literal(UTILITY)
                                .executes(context -> saveServerConfigs(context, UTILITY))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getClient() {
        return Commands.literal(SAVE)
                .then(Commands.literal(CLIENT)
                        .executes(context -> saveClientConfigs(context, null))
                        .then(Commands.literal(RENDER)
                                .executes(context -> saveClientConfigs(context, RENDER)))
                        .then(Commands.literal(DISPLAY)
                                .executes(context -> saveClientConfigs(context, DISPLAY))));
    }

    private static int saveAllConfigs(CommandContext<CommandSourceStack> context) {
        BattleRoyale.getModConfigManager().saveAllConfigs();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.all_config_saved"), true);
        BattleRoyale.LOGGER.info("Saved all {} configs", BattleRoyale.MOD_ID);
        return Command.SINGLE_SUCCESS;
    }

    private static int saveLootConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigSubManager<?> lootConfigManager = getConfigSubManager(context, LootConfigManager.get().getNameKey());
        if (lootConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = lootConfigManager.saveAllConfigs();
            messageKey = "battleroyale.message.loot_config_saved";
        } else {
            int folderId;
            switch (subType) {
                case LOOT_SPAWNER:
                    folderId = LootConfigTypeEnum.LOOT_SPAWNER;
                    messageKey = "battleroyale.message.loot_spawner_config_saved";
                    break;
                case ENTITY_SPAWNER:
                    folderId = LootConfigTypeEnum.ENTITY_SPAWNER;
                    messageKey = "battleroyale.message.entity_spawner_config_saved";
                    break;
                case AIRDROP:
                    folderId = LootConfigTypeEnum.AIRDROP;
                    messageKey = "battleroyale.message.airdrop_config_saved";
                    break;
                case AIRDROP_SPECIAL:
                    folderId = LootConfigTypeEnum.AIRDROP_SPECIAL;
                    messageKey = "battleroyale.message.airdrop_special_config_saved";
                    break;
                case SECRET_ROOM:
                    folderId = LootConfigTypeEnum.SECRET_ROOM;
                    messageKey = "battleroyale.message.secret_room_config_saved";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_loot_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown loot sub-type for save command: {}", subType);
                    return 0;
            }
            success = lootConfigManager.saveConfigs(folderId) ? 1 : 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Saved {} configs via command", subType != null ? subType : "all loot");
        return Command.SINGLE_SUCCESS;
    }

    private static int saveGameConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigManager gameConfigManager = getConfigManager(context, GameConfigManager.get().getNameKey());
        if (gameConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = gameConfigManager.saveAllConfigs();
            messageKey = "battleroyale.message.game_config_saved";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case ZONE:
                    subManagerNameKey = ZoneConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.zone_config_saved";
                    break;
                case SPAWN:
                    subManagerNameKey = SpawnConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.spawn_config_saved";
                    break;
                case GAMERULE:
                    subManagerNameKey = GameruleConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.gamerule_config_saved";
                    break;
                case BOT:
                    subManagerNameKey = BotConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.bot_config_saved";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown game sub-type for save command: {}", subType);
                    return 0;
            }
            IConfigSubManager<?> configSubManager = gameConfigManager.getConfigSubManager(subManagerNameKey);
            if (configSubManager == null) return 0;

            success = configSubManager.saveAllConfigs();
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Saved {} configs via command", subType != null ? subType : "all game");
        return Command.SINGLE_SUCCESS;
    }

    private static int saveEffectConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigManager effectConfigManager = getConfigManager(context, EffectConfigManager.get().getNameKey());
        if (effectConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = effectConfigManager.saveAllConfigs();
            messageKey = "battleroyale.message.effect_config_saved";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case PARTICLE:
                    subManagerNameKey = ParticleConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.particle_config_saved";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_effect_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown effect sub-type for save command: {}", subType);
                    return 0;
            }
            IConfigSubManager<?> configSubManager = effectConfigManager.getConfigSubManager(subManagerNameKey);
            if (configSubManager == null) return 0;

            success = configSubManager.saveAllConfigs();
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Saved {} effect configs via command", subType != null ? subType : "all effect");
        return Command.SINGLE_SUCCESS;
    }

    private static int saveClientConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigManager clientConfigManager = getConfigManager(context, ClientConfigManager.get().getNameKey());
        if (clientConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = clientConfigManager.saveAllConfigs();
            messageKey = "battleroyale.message.client_config_saved";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case RENDER:
                    subManagerNameKey = RenderConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.render_config_saved";
                    break;
                case DISPLAY:
                    subManagerNameKey = DisplayConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.display_config_saved";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_client_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown client sub-type for save command: {}", subType);
                    return 0;
            }
            IConfigSubManager<?> configSubManager = clientConfigManager.getConfigSubManager(subManagerNameKey);
            if (configSubManager == null) return 0;

            success = configSubManager.saveAllConfigs();
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Saved {} client configs via command", subType != null ? subType : "all client");
        return Command.SINGLE_SUCCESS;
    }

    private static int saveServerConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        IConfigManager serverConfigManager = getConfigManager(context, ServerConfigManager.get().getNameKey());
        if (serverConfigManager == null) return 0;

        int success = 0;
        String messageKey;
        if (subType == null) {
            success = serverConfigManager.saveAllConfigs();
            messageKey = "battleroyale.message.server_config_saved";
        } else {
            String subManagerNameKey;
            switch (subType) {
                case PERFORMANCE:
                    subManagerNameKey = PerformanceConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.performance_config_saved";
                    break;
                case UTILITY:
                    subManagerNameKey = UtilityConfigManager.get().getNameKey();
                    messageKey = "battleroyale.message.utility_config_saved";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_server_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown server sub-type for save command: {}", subType);
                    return 0;
            }
            IConfigSubManager<?> configSubManager = serverConfigManager.getConfigSubManager(subManagerNameKey);
            if (configSubManager == null) return 0;

            success = configSubManager.saveAllConfigs();
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Saved {} server configs via command", subType != null ? subType : "all server");
        return Command.SINGLE_SUCCESS;
    }
}