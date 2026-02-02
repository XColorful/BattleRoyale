package xiao.battleroyale.config.common.game.zone;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.game.zone.IZoneConfigManager;
import xiao.battleroyale.api.config.common.game.zone.IZoneSingleEntry;
import xiao.battleroyale.api.config.common.game.zone.ZoneConfigTag;
import xiao.battleroyale.api.config.common.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.config.common.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.config.common.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.config.common.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.config.common.game.zone.special.IZoneSpecialEntry;
import xiao.battleroyale.api.config.common.game.zone.special.ZoneSpecialTag;
import xiao.battleroyale.common.game.zone.GameZoneBuilder;
import xiao.battleroyale.config.AbstractConfigSubManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.FolderConfigData;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.defaultconfigs.DefaultZoneConfigGenerator;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.config.common.game.zone.zonespecial.ZoneSpecialType;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;

public class ZoneConfigManager
        extends AbstractConfigSubManager<ZoneConfigManager.ZoneConfig>
        implements IZoneConfigManager<ZoneConfigManager.ZoneConfig> {

    private static class ZoneConfigManagerHolder {
        private static final ZoneConfigManager INSTANCE = new ZoneConfigManager();
    }

    public static ZoneConfigManager get() {
        return ZoneConfigManagerHolder.INSTANCE;
    }

    private ZoneConfigManager() {
        allFolderConfigData.put(DEFAULT_ZONE_CONFIG_FOLDER, new FolderConfigData<>(DEFAULT_ZONE_CONFIG_FOLDER));
    }

    public static void init() {
        GameConfigManager.get().registerSubManager(get());
    }

    public static final String ZONE_CONFIG_PATH = GameConfigManager.GAME_CONFIG_PATH;
    public static final String ZONE_CONFIG_SUB_PATH = "zone";

    protected final int DEFAULT_ZONE_CONFIG_FOLDER = 0;

    public static class ZoneConfig extends AbstractSingleConfig implements IZoneSingleEntry {
        public static final String CONFIG_TYPE = "ZoneConfig";

        public final int preZoneDelayId;
        public final int zoneDelay;
        public final int zoneTime;
        public final IZoneFuncEntry zoneFuncEntry;
        public final IZoneShapeEntry zoneShapeEntry;
        public @Nullable IZoneSpecialEntry zoneSpecialEntry;

        public ZoneConfig(int zoneId, String zoneName, String zoneColor, int zoneDelay, int zoneTime, IZoneFuncEntry zoneFuncEntry, IZoneShapeEntry zoneShapeEntry) {
            this(zoneId, zoneName, zoneColor, false, -1, zoneDelay, zoneTime, zoneFuncEntry, zoneShapeEntry);
        }
        public ZoneConfig(int zoneId, String zoneName, String zoneColor, int preZoneDelayId, int zoneDelay, int zoneTime, IZoneFuncEntry zoneFuncEntry, IZoneShapeEntry zoneShapeEntry) {
            this(zoneId, zoneName, zoneColor, false, preZoneDelayId, zoneDelay, zoneTime, zoneFuncEntry, zoneShapeEntry);
        }
        public ZoneConfig(int zoneId, String zoneName, String zoneColor, boolean isDefault, int preZoneDelayId, int zoneDelay, int zoneTime, IZoneFuncEntry zoneFuncEntry, IZoneShapeEntry zoneShapeEntry) {
            this(zoneId, zoneName, zoneColor, isDefault, preZoneDelayId, zoneDelay, zoneTime, zoneFuncEntry, zoneShapeEntry, null);
        }
        public ZoneConfig(int zoneId, String zoneName, String zoneColor, boolean isDefault, int preZoneDelayId, int zoneDelay, int zoneTime, IZoneFuncEntry zoneFuncEntry, IZoneShapeEntry zoneShapeEntry, @Nullable IZoneSpecialEntry zoneSpecialEntry) {
            super(zoneId, zoneName, zoneColor, isDefault);
            this.preZoneDelayId = preZoneDelayId;
            this.zoneDelay = zoneDelay;
            this.zoneTime = zoneTime;
            this.zoneFuncEntry = zoneFuncEntry;
            this.zoneShapeEntry = zoneShapeEntry;
            this.zoneSpecialEntry = zoneSpecialEntry;
        }
        @Override public @NotNull ZoneConfig copy() {
            return new ZoneConfig(id, name, color, isDefault, preZoneDelayId, zoneDelay, zoneTime, zoneFuncEntry.copy(), zoneShapeEntry.copy(), zoneSpecialEntry.copy());
        }

        public int getZoneId() {
            return id;
        }
        public String getZoneName() {
            return name;
        }
        public String getZoneColor() { // 暂时用配置颜色作为区域颜色
            return color;
        }
        public int getPreZoneDelayId() {
            return preZoneDelayId;
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
        public @Nullable IZoneSpecialEntry getZoneSpecialEntry() {
            return zoneSpecialEntry;
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
            jsonObject.addProperty(ZoneConfigTag.ZONE_ID, id);
            if (isDefault) {
                jsonObject.addProperty(ZoneConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(ZoneConfigTag.ZONE_NAME, name);
            jsonObject.addProperty(ZoneConfigTag.ZONE_COLOR, color);
            if (preZoneDelayId > -1) {
                jsonObject.addProperty(ZoneConfigTag.PREVIOUS_ZONE_DELAY_ID, preZoneDelayId);
            }
            jsonObject.addProperty(ZoneConfigTag.ZONE_DELAY, zoneDelay);
            jsonObject.addProperty(ZoneConfigTag.ZONE_TIME, zoneTime);
            if (zoneFuncEntry != null) {
                jsonObject.add(ZoneConfigTag.ZONE_FUNC, zoneFuncEntry.toJson());
            }
            if (zoneShapeEntry != null) {
                jsonObject.add(ZoneConfigTag.ZONE_SHAPE, zoneShapeEntry.toJson());
            }
            if (zoneSpecialEntry != null) {
                jsonObject.add(ZoneConfigTag.ZONE_SPECIAL, zoneSpecialEntry.toJson());
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

        public static IZoneSpecialEntry deserializeZoneSpecialEntry(JsonObject jsonObject) {
            try {
                ZoneSpecialType zoneSpecialType = ZoneSpecialType.fromName(JsonUtils.getJsonString(jsonObject, ZoneSpecialTag.TYPE_NAME, ""));
                if (zoneSpecialType != null) {
                    return zoneSpecialType.getDeserializer().apply(jsonObject);
                } else {
                    BattleRoyale.LOGGER.error("Skipped invalid ZoneSpecialEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize ZoneSpecialEntry: {}", e.getMessage());
                return null;
            }
        }
    }


    @Override protected Comparator<ZoneConfig> getConfigIdComparator(int folderId) {
        return Comparator.comparingInt(ZoneConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int folderId) {
        return ZoneConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public boolean generateDefaultConfigs() {
        return generateDefaultConfigs(DEFAULT_ZONE_CONFIG_FOLDER);
    }

    @Override public boolean generateDefaultConfigs(int folderId) {
        return DefaultZoneConfigGenerator.generateAllDefaultConfigs(String.valueOf(getConfigDirPath()));
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_ZONE_CONFIG_FOLDER);
    }
    @Override public boolean setDefaultConfigId(int id) {
        return false;
    }
    @Override public boolean setDefaultConfigId(int folderId, int id) {
        return false;
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public ZoneConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int zoneId = JsonUtils.getJsonInt(configObject, ZoneConfigTag.ZONE_ID, -1);
            JsonObject zoneFuncObject = JsonUtils.getJsonObject(configObject, ZoneConfigTag.ZONE_FUNC, null);
            JsonObject zoneShapeObject = JsonUtils.getJsonObject(configObject, ZoneConfigTag.ZONE_SHAPE, null);
            JsonObject zoneSpecialObject = JsonUtils.getJsonObject(configObject, ZoneConfigTag.ZONE_SPECIAL, null);
            if (zoneId < 0 || zoneFuncObject == null || zoneShapeObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid zone config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, ZoneConfigTag.DEFAULT, false);
            String zoneName = JsonUtils.getJsonString(configObject, ZoneConfigTag.ZONE_NAME, "");
            String zoneColor = JsonUtils.getJsonString(configObject, ZoneConfigTag.ZONE_COLOR, "#0000FF");
            int preZoneDelayId = JsonUtils.getJsonInt(configObject, ZoneConfigTag.PREVIOUS_ZONE_DELAY_ID, -1);
            int zoneDelay = JsonUtils.getJsonInt(configObject, ZoneConfigTag.ZONE_DELAY, 0);
            int zoneTime = JsonUtils.getJsonInt(configObject, ZoneConfigTag.ZONE_TIME, 0);
            IZoneFuncEntry zoneFuncEntry = ZoneConfig.deserializeZoneFuncEntry(zoneFuncObject);
            IZoneShapeEntry zoneShapeEntry = ZoneConfig.deserializeZoneShapeEntry(zoneShapeObject);
            IZoneSpecialEntry zoneSpecialEntry = zoneSpecialObject != null ? ZoneConfig.deserializeZoneSpecialEntry(zoneSpecialObject) : null; // 显式允许null
            if (zoneFuncEntry == null || zoneShapeEntry == null) {
                BattleRoyale.LOGGER.error("Failed to deserialize zone entry for id: {} in {}", zoneId, filePath);
                return null;
            }

            return new ZoneConfig(zoneId, zoneName, zoneColor, isDefault, preZoneDelayId, zoneDelay, zoneTime, zoneFuncEntry, zoneShapeEntry, zoneSpecialEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return ZONE_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return ZONE_CONFIG_SUB_PATH;
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_ZONE_CONFIG_FOLDER);
    }
}