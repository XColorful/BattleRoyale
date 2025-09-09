package xiao.battleroyale.config.client.render;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.IRenderSingleEntry;
import xiao.battleroyale.api.client.render.RenderConfigTag;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.client.render.defaultconfigs.DefaultRenderConfigGenerator;
import xiao.battleroyale.config.client.render.type.BlockEntry;
import xiao.battleroyale.config.client.render.type.SpectateEntry;
import xiao.battleroyale.config.client.render.type.TeamEntry;
import xiao.battleroyale.config.client.render.type.ZoneEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class RenderConfigManager extends AbstractConfigManager<RenderConfigManager.RenderConfig> {

    private static class RenderConfigManagerHolder {
        private static final RenderConfigManager INSTANCE = new RenderConfigManager();
    }

    public static RenderConfigManager get() {
        return RenderConfigManagerHolder.INSTANCE;
    }

    private RenderConfigManager() {
        allFolderConfigData.put(DEFAULT_RENDER_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadRenderConfigs();
    }

    public static final String RENDER_CONFIG_PATH = ClientConfigManager.CLIENT_CONFIG_PATH;
    public static final String RENDER_CONFIG_SUB_PATH = "render";

    protected final int DEFAULT_RENDER_CONFIG_FOLDER = 0;

    public static class RenderConfig extends AbstractSingleConfig implements IRenderSingleEntry {
        public static final String CONFIG_TYPE = "RenderConfig";

        public final BlockEntry blockEntry;
        public final ZoneEntry zoneEntry;
        public final TeamEntry teamEntry;
        public final SpectateEntry spectateEntry;

        public RenderConfig(int id, String name, String color,
                            BlockEntry blockEntry, ZoneEntry zoneEntry, TeamEntry teamEntry, SpectateEntry spectateEntry) {
            this(id, name, color, false, blockEntry, zoneEntry, teamEntry, spectateEntry);
        }

        public RenderConfig(int id, String name, String color, boolean isDefault,
                            BlockEntry blockEntry, ZoneEntry zoneEntry, TeamEntry teamEntry, SpectateEntry spectateEntry) {
            super(id, name, color, isDefault);
            this.blockEntry = blockEntry;
            this.zoneEntry = zoneEntry;
            this.teamEntry = teamEntry;
            this.spectateEntry = spectateEntry;
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(RenderConfigTag.ID, id);
            if (isDefault) {
                jsonObject.addProperty(RenderConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(RenderConfigTag.NAME, name);
            jsonObject.addProperty(RenderConfigTag.COLOR, color);
            if (blockEntry != null) {
                jsonObject.add(RenderConfigTag.BLOCK_ENTRY, blockEntry.toJson());
            }
            if (zoneEntry != null) {
                jsonObject.add(RenderConfigTag.ZONE_ENTRY, zoneEntry.toJson());
            }
            if (teamEntry != null) {
                jsonObject.add(RenderConfigTag.TEAM_ENTRY, teamEntry.toJson());
            }
            if (spectateEntry != null) {
                jsonObject.add(RenderConfigTag.SPECTATE_ENTRY, spectateEntry.toJson());
            }

            return jsonObject;
        }

        public static BlockEntry deserializeBlockEntry(JsonObject jsonObject) {
            try {
                BlockEntry blockEntry = BlockEntry.fromJson(jsonObject);
                if (blockEntry != null) {
                    return blockEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid BlockEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize BlockEntry: {}", e.getMessage());
                return null;
            }
        }

        public static ZoneEntry deserializeZoneEntry(JsonObject jsonObject) {
            try {
                ZoneEntry zoneEntry = ZoneEntry.fromJson(jsonObject);
                if (zoneEntry != null) {
                    return zoneEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid ZoneEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize ZoneEntry: {}", e.getMessage());
                return null;
            }
        }

        public static TeamEntry deserializeTeamEntry(JsonObject jsonObject) {
            try {
                TeamEntry teamEntry = TeamEntry.fromJson(jsonObject);
                if (teamEntry != null) {
                    return teamEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid TeamEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize TeamEntry: {}", e.getMessage());
                return null;
            }
        }

        public static SpectateEntry deserializeSpectateEntry(JsonObject jsonObject) {
            try {
                SpectateEntry spectateEntry = SpectateEntry.fromJson(jsonObject);
                if (spectateEntry != null) {
                    return spectateEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid SpectateEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize SpectateEntry: {}", e.getMessage());
                return null;
            }
        }

        @Override
        public void applyDefault() {
            blockEntry.applyDefault();
            zoneEntry.applyDefault();
            teamEntry.applyDefault();
            spectateEntry.applyDefault();
        }
    }

    @Override protected Comparator<RenderConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(RenderConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int folderId) {
        return RenderConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_RENDER_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int folderId) {
        DefaultRenderConfigGenerator.generateDefaultRenderConfigs();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_RENDER_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public RenderConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, RenderConfigTag.ID, -1);
            JsonObject blockEntryObject = JsonUtils.getJsonObject(configObject, RenderConfigTag.BLOCK_ENTRY, null);
            JsonObject zoneEntryObject = JsonUtils.getJsonObject(configObject, RenderConfigTag.ZONE_ENTRY, null);
            JsonObject teamEntryObject = JsonUtils.getJsonObject(configObject, RenderConfigTag.TEAM_ENTRY, null);
            JsonObject spectateEntryObject = JsonUtils.getJsonObject(configObject, RenderConfigTag.SPECTATE_ENTRY, null);
            if (id < 0 || blockEntryObject == null || zoneEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid render config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, RenderConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, RenderConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, RenderConfigTag.COLOR, "#FFFFFF");
            BlockEntry blockEntry = RenderConfig.deserializeBlockEntry(blockEntryObject);
            ZoneEntry zoneEntry = RenderConfig.deserializeZoneEntry(zoneEntryObject);
            TeamEntry teamEntry = RenderConfig.deserializeTeamEntry(teamEntryObject);
            SpectateEntry spectateEntry = RenderConfig.deserializeSpectateEntry(spectateEntryObject);
            if (blockEntry == null || zoneEntry == null || teamEntry == null || spectateEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize render entry for id: {} in {}", id, filePath);
                return null;
            }

            return new RenderConfig(id, name, color, isDefault, blockEntry, zoneEntry, teamEntry, spectateEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return RENDER_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return RENDER_CONFIG_SUB_PATH;
    }

    /**
     * 特定类别的获取接口
     */
    public RenderConfig getRenderConfig(int id) {
        return getConfigEntry(id, DEFAULT_RENDER_CONFIG_FOLDER);
    }
    public List<RenderConfig> getRenderConfigList() {
        return getConfigEntryList(DEFAULT_RENDER_CONFIG_FOLDER);
    }

    /**
     * 特定类别的重新获取接口
     */
    public void reloadRenderConfigs() {
        reloadConfigs(DEFAULT_RENDER_CONFIG_FOLDER);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_RENDER_CONFIG_FOLDER);
    }
}
