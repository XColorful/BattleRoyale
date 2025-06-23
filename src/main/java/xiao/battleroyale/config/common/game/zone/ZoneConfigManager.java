package xiao.battleroyale.config.common.game.zone;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.*;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.GameZoneBuilder;
import xiao.battleroyale.config.common.AbstractConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.defaultconfigs.DefaultZoneConfigGenerator;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.*;

public class ZoneConfigManager extends AbstractConfigManager<ZoneConfigManager.ZoneConfig> {

    private static class ZoneConfigManagerHolder {
        private static final ZoneConfigManager INSTANCE = new ZoneConfigManager();
    }

    public static ZoneConfigManager get() {
        return ZoneConfigManagerHolder.INSTANCE;
    }

    private ZoneConfigManager() {
        allFolderConfigData.put(DEFAULT_ZONE_CONFIG_FOLDER, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadZoneConfigs();
    }

    public static final String ZONE_CONFIG_PATH = GameConfigManager.GAME_CONFIG_PATH;
    public static final String ZONE_CONFIG_SUB_PATH = "zone";

    protected final int DEFAULT_ZONE_CONFIG_FOLDER = 0;

    public static class ZoneConfig implements IZoneSingleEntry {
        public static final String CONFIG_TYPE = "ZoneConfig";

        public final int zoneId;
        private final String zoneName;
        private final String zoneColor;
        private final int zoneDelay;
        private final int zoneTime;
        private final IZoneFuncEntry zoneFuncEntry;
        private final IZoneShapeEntry zoneShapeEntry;

        public ZoneConfig(int zoneId, String zoneName, String zoneColor, int zoneDelay, int zoneTime, IZoneFuncEntry zoneFuncEntry, IZoneShapeEntry zoneShapeEntry) {
            this.zoneId = zoneId;
            this.zoneName = zoneName;
            this.zoneColor = zoneColor;
            this.zoneDelay = zoneDelay;
            this.zoneTime = zoneTime;
            this.zoneFuncEntry = zoneFuncEntry;
            this.zoneShapeEntry = zoneShapeEntry;
        }

        public int getZoneId() {
            return zoneId;
        }
        public String getZoneName() {
            return zoneName;
        }
        public String getColor() {
            return zoneColor;
        }
        public int getZoneDelay() {
            return zoneDelay;
        }
        public int getZoneTime() {
            return zoneTime;
        }
        public IZoneFuncEntry getZoneFuncEntry() {
            return zoneFuncEntry;
        }
        public IZoneShapeEntry getZoneShapeEntry() {
            return zoneShapeEntry;
        }

        @Nullable
        public IGameZone generateZone() {
            return new GameZoneBuilder()
                    .withZoneConfig(this)
                    .build();
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(ZoneConfigTag.ZONE_ID, zoneId);
            jsonObject.addProperty(ZoneConfigTag.ZONE_NAME, zoneName);
            jsonObject.addProperty(ZoneConfigTag.ZONE_COLOR, zoneColor);
            jsonObject.addProperty(ZoneConfigTag.ZONE_DELAY, zoneDelay);
            jsonObject.addProperty(ZoneConfigTag.ZONE_TIME, zoneTime);
            if (zoneFuncEntry != null) {
                jsonObject.add(ZoneConfigTag.ZONE_FUNC, zoneFuncEntry.toJson());
            }
            if (zoneShapeEntry != null) {
                jsonObject.add(ZoneConfigTag.ZONE_SHAPE, zoneShapeEntry.toJson());
            }
            return jsonObject;
        }

        @Override
        public int getConfigId() {
            return getZoneId();
        }

        public static IZoneFuncEntry deserializeZoneFuncEntry(JsonObject jsonObject) {
            try {
                ZoneFuncType zoneFuncType = ZoneFuncType.fromName(JsonUtils.getJsonString(jsonObject, ZoneFuncTag.TYPE_NAME, ""));
                if (zoneFuncType != null) {
                    return zoneFuncType.getDeserializer().apply(jsonObject);
                } else {
                    BattleRoyale.LOGGER.error("Skipped invalid ZoneFuncEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize ZoneFuncEntry: {}", e.getMessage());
                return null;
            }
        }

        public static IZoneShapeEntry deserializeZoneShapeEntry(JsonObject jsonObject) {
            try {
                ZoneShapeType zoneShapeType = ZoneShapeType.fromName(JsonUtils.getJsonString(jsonObject, ZoneShapeTag.TYPE_NAME, ""));
                if (zoneShapeType != null) {
                    return zoneShapeType.getDeserializer().apply(jsonObject);
                } else {
                    BattleRoyale.LOGGER.error("Skipped invalid ZoneShapeEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize ZoneShapeEntry: {}", e.getMessage());
                return null;
            }
        }
    }


    @Override protected Comparator<ZoneConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(ZoneConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int configType) {
        return ZoneConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_ZONE_CONFIG_FOLDER);
    }

    @Override public void generateDefaultConfigs(int configType) {
        DefaultZoneConfigGenerator.generateAllDefaultConfigs();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_ZONE_CONFIG_FOLDER);
    }
    @Override public void setDefaultConfigId(int id) {
        return;
    }
    @Override public void setDefaultConfigId(int id, int configType) {
        return;
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public ZoneConfig parseConfigEntry(JsonObject configObject, Path filePath, int configType) {
        try {
            int zoneId = JsonUtils.getJsonInt(configObject, ZoneConfigTag.ZONE_ID, -1);
            JsonObject zoneFuncObject = JsonUtils.getJsonObject(configObject, ZoneConfigTag.ZONE_FUNC, null);
            JsonObject zoneShapeObject = JsonUtils.getJsonObject(configObject, ZoneConfigTag.ZONE_SHAPE, null);
            if (zoneId < 0 || zoneFuncObject == null || zoneShapeObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid zone config in {}", filePath);
                return null;
            }

            String zoneName = JsonUtils.getJsonString(configObject, ZoneConfigTag.ZONE_NAME, "");
            String zoneColor = JsonUtils.getJsonString(configObject, ZoneConfigTag.ZONE_COLOR, "#0000FF");
            int zoneDelay = JsonUtils.getJsonInt(configObject, ZoneConfigTag.ZONE_DELAY, 0);
            int zoneTime = JsonUtils.getJsonInt(configObject, ZoneConfigTag.ZONE_TIME, 0);
            IZoneFuncEntry zoneFuncEntry = ZoneConfig.deserializeZoneFuncEntry(zoneFuncObject);
            IZoneShapeEntry zoneShapeEntry = ZoneConfig.deserializeZoneShapeEntry(zoneShapeObject);
            if (zoneFuncEntry == null || zoneShapeEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize zone entry for id: {} in {}", zoneId, filePath);
                return null;
            }

            return new ZoneConfig(zoneId, zoneName, zoneColor, zoneDelay, zoneTime, zoneFuncEntry, zoneShapeEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int configType) {
        return ZONE_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int configType) {
        return ZONE_CONFIG_SUB_PATH;
    }

    /**
     * 特定类别的获取接口
     */
    public ZoneConfig getZoneConfig(int id) {
        return getConfigEntry(id, DEFAULT_ZONE_CONFIG_FOLDER);
    }
    public List<ZoneConfig> getZoneConfigList() {
        return getConfigEntryList(DEFAULT_ZONE_CONFIG_FOLDER);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadZoneConfigs() {
        reloadConfigs(DEFAULT_ZONE_CONFIG_FOLDER);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_ZONE_CONFIG_FOLDER);
    }
}