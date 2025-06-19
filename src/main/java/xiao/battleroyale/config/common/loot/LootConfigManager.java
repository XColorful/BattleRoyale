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
import xiao.battleroyale.config.common.loot.defaultconfigs.DefaultLootConfigGenerator;
import xiao.battleroyale.config.common.loot.type.LootEntryType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.*;

public class LootConfigManager extends AbstractConfigManager<LootConfigManager.LootConfig> {

    public static final String LOOT_CONFIG_SUB_PATH = "loot";
    public static final String LOOT_CONFIG_PATH = Paths.get(AbstractConfigManager.MOD_CONFIG_PATH).resolve(LOOT_CONFIG_SUB_PATH).toString();

    protected final int DEFAULT_LOOT_CONFIG_DATA_ID = LOOT_SPAWNER;

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
        allConfigData.put(LOOT_SPAWNER, new ConfigData<>());
        allConfigData.put(ENTITY_SPAWNER, new ConfigData<>());
        allConfigData.put(AIRDROP, new ConfigData<>());
        allConfigData.put(AIRDROP_SPECIAL, new ConfigData<>());
        allConfigData.put(SECRET_ROOM, new ConfigData<>());
    }

    public static void init() {
        get().reloadAllLootConfigs();
    }

    /**
     * 目前generateLootData需要手动调用this.entry.generateLootData(Random)
     */
    public static class LootConfig implements IConfigSingleEntry {
        public static final String CONFIG_TYPE = "LootConfig";

        private final int lootId;
        private final String name;
        private final String color;
        private final ILootEntry entry;

        public LootConfig(int lootId, String name, String color, ILootEntry entry) {
            this.lootId = lootId;
            this.name = name;
            this.color = color;
            this.entry = entry;
        }

        public int getLootId() {
            return lootId;
        }
        public String getName() {
            return name;
        }
        public String getColor() {
            return color;
        }
        public ILootEntry getEntry() {
            return entry;
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(LootConfigTag.LOOT_ID, lootId);
            jsonObject.addProperty(LootConfigTag.LOOT_NAME, name);
            jsonObject.addProperty(LootConfigTag.LOOT_COLOR, color);
            if (entry != null) {
                jsonObject.add(LootConfigTag.LOOT_ENTRY, entry.toJson());
            }
            return jsonObject;
        }

        @Override
        public int getConfigId() {
            return getLootId();
        }

        @Nullable
        public static ILootEntry deserializeLootEntry(@Nullable JsonObject jsonObject) {
            if (jsonObject == null) {
                BattleRoyale.LOGGER.warn("jsonObject is null, failed to deserialize LootEntry");
                return null;
            }
            try {
                String type = jsonObject.getAsJsonPrimitive(LootEntryTag.TYPE_NAME).getAsString();
                if (type == null) type = "";
                LootEntryType lootEntryType = LootEntryType.fromName(type);
                if (lootEntryType != null) {
                    return lootEntryType.getDeserializer().apply(jsonObject);
                } else {
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize LootEntry: {}", e.getMessage());
                return null;
            }
        }
    }

    @Override protected Comparator<LootConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(LootConfig::getLootId);
    }

    /**
     * IConfigManager
     */
    @Override public String getConfigType(int configType) {
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
        generateDefaultConfigs(DEFAULT_LOOT_CONFIG_DATA_ID);
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
        return getDefaultConfigId(DEFAULT_LOOT_CONFIG_DATA_ID);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public LootConfig parseConfigEntry(JsonObject configObject, Path filePath, int configType) {
        try {
            int lootId = configObject.has(LootConfigTag.LOOT_ID) ? configObject.getAsJsonPrimitive(LootConfigTag.LOOT_ID).getAsInt() : -1;
            JsonObject lootEntryObject = configObject.has(LootConfigTag.LOOT_ENTRY) ? configObject.getAsJsonObject(LootConfigTag.LOOT_ENTRY) : null;
            if (lootId < 0 || lootEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid loot config in {}", filePath);
                return null;
            }
            String name = configObject.has(LootConfigTag.LOOT_NAME) ? configObject.getAsJsonPrimitive(LootConfigTag.LOOT_NAME).getAsString() : "";
            String color = configObject.has(LootConfigTag.LOOT_COLOR) ? configObject.getAsJsonPrimitive(LootConfigTag.LOOT_COLOR).getAsString() : "#FFFFFF";
            if (name == null) name = "";
            if (color == null) color = "#FFFFFF";
            ILootEntry lootEntry = LootConfig.deserializeLootEntry(lootEntryObject);
            if (lootEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize loot entry for id: {} in {}", lootId, filePath);
                return null;
            }
            return new LootConfig(lootId, name, color, lootEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getConfigType(), filePath, e.getMessage());
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
    public List<LootConfig> getAllLootSpawnerConfigs() {
        return getAllConfigEntries(LOOT_SPAWNER);
    }
    public LootConfig getEntitySpawnerConfig(int id) {
        return getConfigEntry(id, ENTITY_SPAWNER);
    }
    public List<LootConfig> getAllEntitySpawnerConfigs() {
        return getAllConfigEntries(ENTITY_SPAWNER);
    }
    public LootConfig getAirdropConfig(int id) {
        return getConfigEntry(id, AIRDROP);
    }
    public List<LootConfig> getAllAirdropConfigs() {
        return getAllConfigEntries(AIRDROP);
    }
    public LootConfig getSpecialAirdropConfig(int id) {
        return getConfigEntry(id, AIRDROP_SPECIAL);
    }
    public List<LootConfig> getAllSpecialAirdropConfigs() {
        return getAllConfigEntries(AIRDROP_SPECIAL);
    }
    public LootConfig getSecretRoomConfig(int id) {
        return getConfigEntry(id, SECRET_ROOM);
    }
    public List<LootConfig> getAllSecretRoomConfigs() {
        return getAllConfigEntries(SECRET_ROOM);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadAllLootConfigs() {
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
        initializeDefaultConfigsIfEmpty(DEFAULT_LOOT_CONFIG_DATA_ID);
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