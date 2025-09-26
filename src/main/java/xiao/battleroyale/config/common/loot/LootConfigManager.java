package xiao.battleroyale.config.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootConfigTag;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.block.entity.AbstractLootBlockEntity;
import xiao.battleroyale.command.CommandArg;
import xiao.battleroyale.config.AbstractConfigSubManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.ConfigManager;
import xiao.battleroyale.config.FolderConfigData;
import xiao.battleroyale.config.common.loot.defaultconfigs.DefaultLootConfigGenerator;
import xiao.battleroyale.config.common.loot.type.LootEntryType;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.*;

public class LootConfigManager extends AbstractConfigSubManager<LootConfigManager.LootConfig> {

    public static final String LOOT_CONFIG_SUB_PATH = "loot";
    public static final String LOOT_CONFIG_PATH = Paths.get(ConfigManager.MOD_CONFIG_PATH).resolve(LOOT_CONFIG_SUB_PATH).toString();

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
        super(CommandArg.LOOT);
        allFolderConfigData.put(LOOT_SPAWNER, new FolderConfigData<>());
        allFolderConfigData.put(ENTITY_SPAWNER, new FolderConfigData<>());
        allFolderConfigData.put(AIRDROP, new FolderConfigData<>());
        allFolderConfigData.put(AIRDROP_SPECIAL, new FolderConfigData<>());
        allFolderConfigData.put(SECRET_ROOM, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadAllConfigs();
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

    @Override protected Comparator<LootConfig> getConfigIdComparator(int folderId) {
        return Comparator.comparingInt(LootConfig::getConfigId);
    }

    /**
     * IConfigSubManager
     */
    @Override public String getFolderType() {
        return getFolderType(DEFAULT_LOOT_CONFIG_FOLDER);
    }
    @Override public String getFolderType(int folderId) {
        return LootConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_LOOT_CONFIG_FOLDER);
    }
    @Override public void generateDefaultConfigs(int folderId) {
        switch (folderId) {
            case LOOT_SPAWNER -> DefaultLootConfigGenerator.generateDefaultLootSpawnerConfig();
            case ENTITY_SPAWNER -> DefaultLootConfigGenerator.generateDefaultEntitySpawnerConfig();
            case AIRDROP -> DefaultLootConfigGenerator.generateDefaultAirdropConfig();
            case AIRDROP_SPECIAL -> DefaultLootConfigGenerator.generateDefaultAirdropSpecialConfig();
            case SECRET_ROOM -> DefaultLootConfigGenerator.generateDefaultSecretRoomConfig();
            case ALL_LOOT -> {
                DefaultLootConfigGenerator.generateAllDefaultConfigs();
            }
            default -> DefaultLootConfigGenerator.generateDefaultLootSpawnerConfig();
        }
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public LootConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int lootId = JsonUtils.getJsonInt(configObject, LootConfigTag.LOOT_ID, -1);
            JsonObject lootEntryObject = JsonUtils.getJsonObject(configObject, LootConfigTag.LOOT_ENTRY, null);
            if (lootId < 0 || lootEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid loot config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, LootConfigTag.DEFAULT, false);
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
    @Override public String getConfigPath(int folderId) {
        return LOOT_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return switch (folderId) {
            case LOOT_SPAWNER -> LOOT_SPAWNER_CONFIG_SUB_PATH;
            case ENTITY_SPAWNER -> ENTITY_SPAWNER_CONFIG_SUB_PATH;
            case AIRDROP -> AIRDROP_CONFIG_SUB_PATH;
            case AIRDROP_SPECIAL -> AIRDROP_SPECIAL_CONFIG_SUB_PATH;
            case SECRET_ROOM -> SECRET_ROOM_CONFIG_SUB_PATH;
            default -> LOOT_SPAWNER_CONFIG_SUB_PATH;
        };
    }
    @Override public void initializeDefaultConfigsIfEmpty() {
        initializeDefaultConfigsIfEmpty(DEFAULT_LOOT_CONFIG_FOLDER);
    }
    @Override public void initializeDefaultConfigsIfEmpty(int folderId) {
        switch (folderId) {
            case LootConfigTypeEnum.LOOT_SPAWNER -> super.initializeDefaultConfigsIfEmpty(LootConfigTypeEnum.LOOT_SPAWNER);
            case LootConfigTypeEnum.ENTITY_SPAWNER -> super.initializeDefaultConfigsIfEmpty(LootConfigTypeEnum.ENTITY_SPAWNER);
            case LootConfigTypeEnum.AIRDROP -> super.initializeDefaultConfigsIfEmpty(LootConfigTypeEnum.AIRDROP);
            case LootConfigTypeEnum.AIRDROP_SPECIAL -> super.initializeDefaultConfigsIfEmpty(LootConfigTypeEnum.AIRDROP_SPECIAL);
            case LootConfigTypeEnum.SECRET_ROOM -> super.initializeDefaultConfigsIfEmpty(LootConfigTypeEnum.SECRET_ROOM);
            default -> super.initializeDefaultConfigsIfEmpty(LOOT_SPAWNER);
        }
    }

    /**
     * 根据刷新实体/方块自身lootId的通用获取接口
     */
    @Nullable
    public LootConfig getLootConfig(BlockEntity be, int id) {
        if (be instanceof AbstractLootBlockEntity lootBlockEntity) {
            return this.getConfigEntry(lootBlockEntity.getConfigFolderId(), id);
        }
        BattleRoyale.LOGGER.warn("unsupported BlockEntity type");
        return null;
    }
}