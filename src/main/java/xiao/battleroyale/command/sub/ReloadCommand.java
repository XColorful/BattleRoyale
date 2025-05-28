package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.BattleRoyale;

import javax.annotation.Nullable;

public class ReloadCommand {
    private static final String RELOAD_NAME = "reload";

    private static final String LOOT_NAME = "loot";
    private static final String GAME_NAME = "game";

    // Loot 子类配置
    private static final String LOOT_SPAWNER_NAME = "loot_spawner";
    private static final String ENTITY_SPAWNER_NAME = "entity_spawner";
    private static final String AIRDROP_NAME = "airdrop";
    private static final String AIRDROP_SPECIAL_NAME = "airdrop_special";
    private static final String SECRET_ROOM_NAME = "secret_room";

    // Game 子类配置
    private static final String ZONE_NAME = "zone";
    private static final String SPAWN_NAME = "spawn";
    private static final String GAMERULE_NAME = "gamerule";
    private static final String BOT = "bot";


    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(RELOAD_NAME)
                .executes(ReloadCommand::reloadAllConfigs)
                .then(Commands.literal(LOOT_NAME)
                        .executes(context -> reloadLootConfigs(context, null))
                        .then(Commands.literal(LOOT_SPAWNER_NAME)
                                .executes(context -> reloadLootConfigs(context, LOOT_SPAWNER_NAME)))
                        .then(Commands.literal(ENTITY_SPAWNER_NAME)
                                .executes(context -> reloadLootConfigs(context, ENTITY_SPAWNER_NAME)))
                        .then(Commands.literal(AIRDROP_NAME)
                                .executes(context -> reloadLootConfigs(context, AIRDROP_NAME)))
                        .then(Commands.literal(AIRDROP_SPECIAL_NAME)
                                .executes(context -> reloadLootConfigs(context, AIRDROP_SPECIAL_NAME)))
                        .then(Commands.literal(SECRET_ROOM_NAME)
                                .executes(context -> reloadLootConfigs(context, SECRET_ROOM_NAME))))
                .then(Commands.literal(GAME_NAME)
                        .executes(context -> reloadGameConfigs(context, null))
                        .then(Commands.literal(ZONE_NAME)
                                .executes(context -> reloadGameConfigs(context, ZONE_NAME)))
                        .then(Commands.literal(SPAWN_NAME)
                                .executes(context -> reloadGameConfigs(context, SPAWN_NAME)))
                        .then(Commands.literal(GAMERULE_NAME)
                                .executes(context -> reloadGameConfigs(context, GAMERULE_NAME))));
    }

    private static int reloadAllConfigs(CommandContext<CommandSourceStack> context) {
        LootConfigManager.get().reloadConfigs();
        GameConfigManager.get().reloadConfigs();

        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.all_configs_reloaded"), true);
        BattleRoyale.LOGGER.info("Reloaded all battleroyale configs");
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadLootConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            LootConfigManager.get().reloadConfigs();
            messageKey = "battleroyale.message.loot_config_reloaded";
        } else {
            switch (subType) {
                case LOOT_SPAWNER_NAME:
                    LootConfigManager.get().loadLootSpawnerConfigs();
                    messageKey = "battleroyale.message.loot_spawner_config_reloaded";
                    break;
                case ENTITY_SPAWNER_NAME:
                    LootConfigManager.get().loadEntitySpawnerConfigs();
                    messageKey = "battleroyale.message.entity_spawner_config_reloaded";
                    break;
                case AIRDROP_NAME:
                    LootConfigManager.get().loadAirdropConfigs();
                    messageKey = "battleroyale.message.airdrop_config_reloaded";
                    break;
                case AIRDROP_SPECIAL_NAME:
                    LootConfigManager.get().loadAirdropSpecialConfigs();
                    messageKey = "battleroyale.message.airdrop_special_config_reloaded";
                    break;
                case SECRET_ROOM_NAME:
                    LootConfigManager.get().loadSecretRoomConfigs();
                    messageKey = "battleroyale.message.secret_room_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_loot_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown loot sub-type for reload command: {}", subType);
                    return 0;
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Reloaded {} loot configs via command", subType != null ? subType : "all loot");
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadGameConfigs(CommandContext<CommandSourceStack> context, @Nullable String subType) {
        String messageKey;
        if (subType == null) {
            GameConfigManager.get().reloadConfigs();
            messageKey = "battleroyale.message.game_config_reloaded";
        } else {
            switch (subType) {
                case ZONE_NAME:
                    GameConfigManager.get().loadZoneConfigs();
                    messageKey = "battleroyale.message.zone_config_reloaded";
                    break;
                case SPAWN_NAME:
                    GameConfigManager.get().loadSpawnConfigs();
                    messageKey = "battleroyale.message.spawn_config_reloaded";
                    break;
                case GAMERULE_NAME:
                    GameConfigManager.get().loadGameruleConfigs();
                    messageKey = "battleroyale.message.gamerule_config_reloaded";
                    break;
                default:
                    context.getSource().sendFailure(Component.translatable("battleroyale.message.unknown_game_sub_type", subType));
                    BattleRoyale.LOGGER.warn("Unknown game sub-type for reload command: {}", subType);
                    return 0;
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable(messageKey), true);
        BattleRoyale.LOGGER.info("Reloaded {} game configs via command", subType != null ? subType : "all game");
        return Command.SINGLE_SUCCESS;
    }
}