package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.BattleRoyale;
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
                                .executes(context -> reloadEffectConfigs(context, PARTICLE))));
    }

    private static int reloadAllConfigs(CommandContext<CommandSourceStack> context) {
        LootConfigManager.get().reloadAllLootConfigs();
        GameConfigManager.get().reloadAllConfigs();
        EffectConfigManager.get().reloadAllConfigs();

        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.all_configs_reloaded"), true);
        BattleRoyale.LOGGER.info("Reloaded all {} configs", BattleRoyale.MOD_ID);
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadLootConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            LootConfigManager.get().reloadAllLootConfigs();
            messageKey = "battleroyale.message.loot_config_reloaded";
        } else {
            switch (subType) {
                case LOOT_SPAWNER:
                    LootConfigManager.get().reloadLootSpawnerConfigs();
                    messageKey = "battleroyale.message.loot_spawner_config_reloaded";
                    break;
                case ENTITY_SPAWNER:
                    LootConfigManager.get().reloadEntitySpawnerConfigs();
                    messageKey = "battleroyale.message.entity_spawner_config_reloaded";
                    break;
                case AIRDROP:
                    LootConfigManager.get().reloadAirdropConfigs();
                    messageKey = "battleroyale.message.airdrop_config_reloaded";
                    break;
                case AIRDROP_SPECIAL:
                    LootConfigManager.get().reloadAirdropSpecialConfigs();
                    messageKey = "battleroyale.message.airdrop_special_config_reloaded";
                    break;
                case SECRET_ROOM:
                    LootConfigManager.get().reloadSecretRoomConfigs();
                    messageKey = "battleroyale.message.secret_room_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_loot_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown loot sub-type for reload command: {}", subType);
                    return 0;
            }
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
            switch (subType) {
                case ZONE:
                    GameConfigManager.get().reloadZoneConfigs();
                    messageKey = "battleroyale.message.zone_config_reloaded";
                    break;
                case SPAWN:
                    GameConfigManager.get().reloadSpawnConfigs();
                    messageKey = "battleroyale.message.spawn_config_reloaded";
                    break;
                case GAMERULE:
                    GameConfigManager.get().reloadGameruleConfigs();
                    messageKey = "battleroyale.message.gamerule_config_reloaded";
                    break;
                case BOT:
                    GameConfigManager.get().reloadBotConfigs();
                    messageKey = "battleroyale.message.bot_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown game sub-type for reload command: {}", subType);
                    return 0;
            }
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
            switch (subType) {
                case PARTICLE:
                    EffectConfigManager.get().reloadParticleConfigs();
                    messageKey = "battleroyale.message.particle_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    return 0;
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Reloaded {} effect configs via command", subType != null ? subType : "all effect");
        return Command.SINGLE_SUCCESS;
    }
}