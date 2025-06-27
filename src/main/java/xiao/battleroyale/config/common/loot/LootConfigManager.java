package xiao.battleroyale.config.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.IConfigSingleEntry;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootConfigTag;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.config.common.AbstractConfigManager;
import xiao.battleroyale.config.common.AbstractSingleConfig;
import xiao.battleroyale.config.common.loot.defaultconfigs.DefaultLootConfigGenerator;
import xiao.battleroyale.config.common.loot.type.LootEntryType;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.*;

public class LootConfigManager extends AbstractConfigManager<LootConfigManager.LootConfig> {

    public static final String LOOT_CONFIG_SUB_PATH = "loot";
    public static final String LOOT_CONFIG_PATH = Paths.get(AbstractConfigManager.MOD_CONFIG_PATH).resolve(LOOT_CONFIG_SUB_PATH).toString();

    protected final int DEFAULT_LOOT_CONFIG_FOLDER = LOOT_SPAWNER;

    public static final String LOOT_SPAWNER_CONFIG_SUB_PATH = "loot_spawner";
    public static final String ENTITY_SPAWNER_CONFIG_SUB_PATH = "entity_spawner";
    public static final String AIRDROP_CONFIG_SUB_PATH = "airdrop";
    public static final String AIRDROP_SPECIAL_CONFIG_SUB_PATH = "airdrop_special";
    public static final String SECRET_ROOM_CONFIG_SUB_PATH = "secret_room";

    private static class LootConfigManagerHolder {
        private static final LootConfigManager INSTANCE = new LootConfigManager();
    }

    public static LootConfigManager get() {
        return LootConfigManagerHolder.INSTANCE;
    }


    private LootConfigManager() {
        allFolderConfigData.put(LOOT_SPAWNER, new FolderConfigData<>());
        allFolderConfigData.put(ENTITY_SPAWNER, new FolderConfigData<>());
        allFolderConfigData.put(AIRDROP, new FolderConfigData<>());
        allFolderConfigData.put(AIRDROP_SPECIAL, new FolderConfigData<>());
        allFolderConfigData.put(SECRET_ROOM, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadAllLootConfigs();
    }

    /**
     * 目前generateLootData需要手动调用this.entry.generateLootData(Random)
     */
    public static class LootConfig extends AbstractSingleConfig {
        public static final String CONFIG_TYPE = "LootConfig";

        public final ILootEntry entry;

        public LootConfig(int lootId, String name, String color, ILootEntry entry) {
            this(lootId, name, color, false, entry);
        }

        public LootConfig(int lootId, String name, String color, boolean isDefault, ILootEntry entry) {
            super(lootId, name, color, isDefault);
            this.entry = entry;
        }

    @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(LootConfigTag.LOOT_ID, id);
            if (isDefault) {
                jsonObject.addProperty(LootConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(LootConfigTag.LOOT_NAME, name);
            jsonObject.addProperty(LootConfigTag.LOOT_COLOR, color);
            if (entry != null) {
                jsonObject.add(LootConfigTag.LOOT_ENTRY, entry.toJson());
            }
            return jsonObject;
        }

        @Nullable
        public static ILootEntry deserializeLootEntry(@Nullable JsonObject jsonObject) {
            try {
                LootEntryType lootEntryType = LootEntryType.fromName(JsonUtils.getJsonString(jsonObject, LootEntryTag.TYPE_NAME, ""));
                if (lootEntryType != null) {
                    return lootEntryType.getDeserializer().apply(jsonObject);
                } else {
                    BattleRoyale.LOGGER.info("Skipped invalid LootEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize LootEntry: {}", e.getMessage());
                return null;
            }
        }
    }

    @Override protected Comparator<LootConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(LootConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int configType) {
        return LootConfig.CONFIG_TYPE;
    }
    public String getLootSpawnerConfigEntryFileName() {
        return getConfigEntryFileName(LOOT_SPAWNER);
    }
    public String getEntitySpawnerConfigEntryFileName() {
        return getConfigEntryFileName(ENTITY_SPAWNER);
    }
    public String getAirdropConfigEntryFileName() {
        return getConfigEntryFileName(AIRDROP);
    }
    public String getAirdropSpecialConfigEntryFileName() {
        return getConfigEntryFileName(AIRDROP_SPECIAL);
    }
    public String getSecretRoomConfigEntryFileName() {
        return getConfigEntryFileName(SECRET_ROOM);
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_LOOT_CONFIG_FOLDER);
    }
    @Override public void generateDefaultConfigs(int configType) {
        switch (configType) {
            case LOOT_SPAWNER -> DefaultLootConfigGenerator.generateDefaultLootSpawnerConfig();
            case ENTITY_SPAWNER -> DefaultLootConfigGenerator.generateDefaultEntitySpawnerConfig();
            case AIRDROP -> DefaultLootConfigGenerator.generateDefaultAirdropConfig();
            case AIRDROP_SPECIAL -> DefaultLootConfigGenerator.generateDefaultAirdropSpecialConfig();
            case SECRET_ROOM -> DefaultLootConfigGenerator.generateDefaultSecretRoomConfig();
            default -> DefaultLootConfigGenerator.generateDefaultLootSpawnerConfig();
        }
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_LOOT_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public LootConfig parseConfigEntry(JsonObject configObject, Path filePath, int configType) {
        try {
            int lootId = JsonUtils.getJsonInt(configObject, LootConfigTag.LOOT_ID, -1);
            JsonObject lootEntryObject = JsonUtils.getJsonObject(configObject, LootConfigTag.LOOT_ENTRY, null);
            if (lootId < 0 || lootEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid loot config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBoolean(configObject, LootConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, LootConfigTag.LOOT_NAME, "");
            String color = JsonUtils.getJsonString(configObject, LootConfigTag.LOOT_COLOR, "#FFFFFF");
            ILootEntry lootEntry = LootConfig.deserializeLootEntry(lootEntryObject);
            if (lootEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize loot entry for id: {} in {}", lootId, filePath);
                return null;
            }
            return new LootConfig(lootId, name, color, isDefault, lootEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int configType) {
        return LOOT_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int configType) {
        return switch (configType) {
            case LOOT_SPAWNER -> LOOT_SPAWNER_CONFIG_SUB_PATH;
            case ENTITY_SPAWNER -> ENTITY_SPAWNER_CONFIG_SUB_PATH;
            case AIRDROP -> AIRDROP_CONFIG_SUB_PATH;
            case AIRDROP_SPECIAL -> AIRDROP_SPECIAL_CONFIG_SUB_PATH;
            case SECRET_ROOM -> SECRET_ROOM_CONFIG_SUB_PATH;
            default -> LOOT_SPAWNER_CONFIG_SUB_PATH;
        };
    }

    /**
     * 根据刷新实体/方块自身lootId的通用获取接口
     */
    @Nullable
    public LootConfig getLootConfig(BlockEntity be, int id) {
        if (be instanceof LootSpawnerBlockEntity) {
            return getLootSpawnerConfig(id);
        } else if (be instanceof EntitySpawnerBlockEntity) {
            return getEntitySpawnerConfig(id);
        }
        BattleRoyale.LOGGER.warn("unsupported BlockEntity type");
        return null;
    }

    /**
     * 特定类别的获取接口
     */
    public LootConfig getLootSpawnerConfig(int id) {
        return getConfigEntry(id, LOOT_SPAWNER);
    }
    public List<LootConfig> getLootSpawnerConfigList() {
        return getConfigEntryList(LOOT_SPAWNER);
    }
    public LootConfig getEntitySpawnerConfig(int id) {
        return getConfigEntry(id, ENTITY_SPAWNER);
    }
    public List<LootConfig> getEntitySpawnerConfigList() {
        return getConfigEntryList(ENTITY_SPAWNER);
    }
    public LootConfig getAirdropConfig(int id) {
        return getConfigEntry(id, AIRDROP);
    }
    public List<LootConfig> getAirdropConfigList() {
        return getConfigEntryList(AIRDROP);
    }
    public LootConfig getSpecialAirdropConfig(int id) {
        return getConfigEntry(id, AIRDROP_SPECIAL);
    }
    public List<LootConfig> getSpecialAirdropConfigList() {
        return getConfigEntryList(AIRDROP_SPECIAL);
    }
    public LootConfig getSecretRoomConfig(int id) {
        return getConfigEntry(id, SECRET_ROOM);
    }
    public List<LootConfig> getSecretRoomConfigList() {
        return getConfigEntryList(SECRET_ROOM);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadAllLootConfigs() {
        // 各物资刷新配置均以文件为选择，不需要切换
        reloadLootSpawnerConfigs();
        reloadEntitySpawnerConfigs();
        reloadAirdropConfigs();
        reloadAirdropSpecialConfigs();
        reloadSecretRoomConfigs();
    }
    public void reloadLootSpawnerConfigs() {
        reloadConfigs(LOOT_SPAWNER);
    }
    public void reloadEntitySpawnerConfigs() {
        reloadConfigs(ENTITY_SPAWNER);
    }
    public void reloadAirdropConfigs() {
        reloadConfigs(AIRDROP);
    }
    public void reloadAirdropSpecialConfigs() {
        reloadConfigs(AIRDROP_SPECIAL);
    }
    public void reloadSecretRoomConfigs() {
        reloadConfigs(SECRET_ROOM);
    }

    public boolean switchNextLootSpawnerConfig() {
        return switchConfigFile(LOOT_SPAWNER);
    }
    public boolean switchLootSpawnerConfig(String fileName) {
        return switchConfigFile(fileName, LOOT_SPAWNER);
    }
    public boolean switchNextEntitySpawnerConfig() {
        return switchConfigFile(ENTITY_SPAWNER);
    }
    public boolean switchEntitySpawnerConfig(String fileName) {
        return switchConfigFile(fileName, ENTITY_SPAWNER);
    }
    public boolean switchNextAirdropConfig() {
        return switchConfigFile(AIRDROP);
    }
    public boolean switchAirdropConfig(String fileName) {
        return switchConfigFile(fileName, AIRDROP);
    }
    public boolean switchNextAirdropSpecialConfig() {
        return switchConfigFile(AIRDROP_SPECIAL);
    }
    public boolean switchAirdropSpecialConfig(String fileName) {
        return switchConfigFile(fileName, AIRDROP_SPECIAL);
    }
    public boolean switchNextSecretRoomConfig() {
        return switchConfigFile(SECRET_ROOM);
    }
    public boolean switchSecretRoomConfig(String fileName) {
        return switchConfigFile(fileName, SECRET_ROOM);
    }


    @Override public void initializeDefaultConfigsIfEmpty() {
        initializeDefaultConfigsIfEmpty(DEFAULT_LOOT_CONFIG_FOLDER);
    }
    @Override public void initializeDefaultConfigsIfEmpty(int configType) {
        switch (configType) {
            case LOOT_SPAWNER -> super.initializeDefaultConfigsIfEmpty(LOOT_SPAWNER);
            case ENTITY_SPAWNER -> super.initializeDefaultConfigsIfEmpty(ENTITY_SPAWNER);
            case AIRDROP -> super.initializeDefaultConfigsIfEmpty(AIRDROP);
            case AIRDROP_SPECIAL -> super.initializeDefaultConfigsIfEmpty(AIRDROP_SPECIAL);
            case SECRET_ROOM -> super.initializeDefaultConfigsIfEmpty(SECRET_ROOM);
            default -> super.initializeDefaultConfigsIfEmpty(LOOT_SPAWNER);
        }
    }
}