package xiao.battleroyale.common.server.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.sub.LootCommand;
import xiao.battleroyale.common.server.utility.lootconfig.LootConfigGenerator;
import xiao.battleroyale.common.server.utility.lootconfig.LootTableGenerator;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class ConfigGenerator {

    public static boolean autoLootConfig(@Nullable ServerPlayer player, ServerLevel serverLevel,
                                         int lootId,
                                         String type, Vec3 centerPos,
                                         int repeat, int weight, int radius, boolean autoReload) {
        String startSystemTime = StringUtils.getTimestampString();
        String configEntryName = String.format("lootConfig-%s-%s", startSystemTime, type);
        LootConfig lootConfig = LootConfigGenerator.autoLootConfig(serverLevel,
                lootId, configEntryName,
                type, centerPos,
                repeat, weight, radius);

        boolean isSuccess = lootConfig != null;
        if (isSuccess) {
            if (autoReload) {
                String lootConfigNameKey = LootConfigManager.get().getNameKey();
                IConfigSubManager<?> lootConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(lootConfigNameKey);
                if (lootConfigManager != null) {
                    // 写入文件
                    String filePath = String.valueOf(Paths.get(String.valueOf(lootConfigManager.getConfigDirPath(LootConfigTypeEnum.LOOT_SPAWNER)),  String.format("%s.json", configEntryName)));
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(lootConfig.toJson());
                    isSuccess = writeJsonToFile(filePath, jsonArray);
                    if (isSuccess) {
                        if (player != null) {
                            ChatUtils.sendTranslatableMessageToPlayer(player, "battleroyale.message.utility_lootconfig_generation_success", configEntryName);
                        }
                        // 重载配置
                        if (lootConfigManager.reloadConfigs(LootConfigTypeEnum.LOOT_SPAWNER)) {
                            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.loot_spawner_config_reloaded");
                            BattleRoyale.LOGGER.info("Reloaded {} conffigs via ConfigGenerator autoReload", lootConfigNameKey);
                            // 切换配置
                            if (lootConfigManager.switchConfigFile(LootConfigTypeEnum.LOOT_SPAWNER, configEntryName)) {
                                BattleRoyale.LOGGER.info("Switch loot spawner config file to {} via ConfigGenerator autoReloaded", configEntryName);
                                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.switch_loot_spawner_config_file", configEntryName);
                            }
                        }
                    }
                } else {
                    isSuccess = false;
                    ChatUtils.sendMessageToAllPlayers(serverLevel, "Unexpected exception occurred during autoLootConfig: LootConfigManager is null");
                    BattleRoyale.LOGGER.error("LootConfigManager is null");
                }
            }
        } else {
            BattleRoyale.LOGGER.debug("autoLootConfig is not successful");
        }

        if (!isSuccess && player != null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, "battleroyale.message.utility_lootconfig_generation_failed");
        }
        return isSuccess;
    }

    public static boolean toLootTable(@Nullable CommandSourceStack source, int lootConfigId, String fileName) {
        IConfigSubManager<?> lootConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(LootConfigManager.get().getNameKey());
        if (lootConfigManager == null || !(lootConfigManager.getConfigEntry(lootConfigId) instanceof LootConfig lootConfig)) {
            if (source != null) source.sendFailure(Component.translatable("battleroyale.message.loot_config_not_found"));
            return false;
        }

        JsonObject lootTableJson = LootTableGenerator.toLootTableJson(lootConfig.getLootEntry());
        if (lootTableJson == null) {
            if (source != null) source.sendFailure(Component.translatable("battleroyale.message.no_loot_table_generated"));
            return false;
        }

        try {
            // 获取服务器存档的 datapacks 根目录
            Path datapackRoot = BattleRoyale.getMinecraftServer().getWorldPath(LevelResource.DATAPACK_DIR);

            // 直接定位到名为 battleroyale 的数据包目录下
            // 路径结构：world/datapacks/battleroyale/data/battleroyale/loot_tables/*.json
            String namespace = BattleRoyale.MOD_ID;
            Path modDatapackPath = datapackRoot.resolve(namespace);
            Path lootTablePath = modDatapackPath
                    .resolve("data")
                    .resolve(namespace)
                    .resolve("loot_tables");

            // 写入文件
            String fullFilePath = lootTablePath.resolve(fileName + ".json").toString();
            boolean success = JsonUtils.writeJsonToFile(fullFilePath, lootTableJson);
            if (success) {
                BattleRoyale.LOGGER.info("Generated loot table in {}", fullFilePath);
                if (source != null) {
                    source.sendSuccess(() -> Component.translatable("battleroyale.message.loot_table_generation_success", fullFilePath), true);
                    source.sendSuccess(() -> Component.translatable("battleroyale.message.please_not_use_loot_table", LootCommand.getLootPlayerResetCommand("@s", 0)), false);
                    source.sendFailure(Component.literal("This feature is no longer supported"));
                }
            }

            // 如无 pack.mcmeta 则创建一个，不覆盖已有的
            /*
                {
                  "pack": {
                    "description": "",
                    "pack_format": 15
                  }
                }
             */
            String mcmetaPath = modDatapackPath.resolve("pack.mcmeta").toString();
            File mcmetaFile = new File(mcmetaPath);
            if (!mcmetaFile.exists()) {
                JsonObject mcmetaJson = new JsonObject();
                JsonObject packObject = new JsonObject();
                packObject.addProperty("description", "Custom BattleRoyale Auto Generated Resources");
                packObject.addProperty("pack_format", 48);
                mcmetaJson.add("pack", packObject);

                if (JsonUtils.writeJsonToFile(mcmetaPath, mcmetaJson)) {
                    BattleRoyale.LOGGER.info("Generated missing pack.mcmeta in {}", mcmetaPath);
                }
            }

            return success;

        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to export loot table", e);
            if (source != null) source.sendFailure(Component.translatable("battleroyale.message.loot_table_generation_failed"));
            return false;
        }
    }
}
