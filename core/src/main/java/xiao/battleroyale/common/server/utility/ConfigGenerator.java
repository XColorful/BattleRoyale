package xiao.battleroyale.common.server.utility;

import com.google.gson.JsonArray;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.common.server.utility.lootconfig.LootConfigGenerator;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.StringUtils;

import javax.annotation.Nullable;
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
}
