package xiao.battleroyale.config.common.game.zone;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.*;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.defaultconfigs.DefaultZoneConfigGenerator;
import xiao.battleroyale.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ZoneConfigManager {
    public static final int DEFAULT_CONFIG_ID = 0;

    public static final String ZONE_CONFIG_SUB_PATH = "zone";

    private final Map<Integer, ZoneConfig> zoneConfigs = new HashMap<>();
    private final List<ZoneConfig> allZoneConfigs = new ArrayList<>();

    private static ZoneConfigManager instance;

    private ZoneConfigManager() {
        reloadConfigs();
    }

    public void reloadConfigs() {
        zoneConfigs.clear();
        allZoneConfigs.clear();
        loadZoneConfigs();

        initializeDefaultConfigsIfEmpty();
    }

    public static void init() {
        if (instance == null) {
            instance = new ZoneConfigManager();
        }
    }

    public static ZoneConfigManager get() {
        if (instance == null) {
            ZoneConfigManager.init();
        }
        return instance;
    }

    public ZoneConfig getZoneConfig(int zoneId) {
        return zoneConfigs.get(zoneId);
    }

    public List<ZoneConfig> getAllZoneConfigs() {
        return allZoneConfigs;
    }

    public void loadZoneConfigs() {
        loadConfigsFromDirectory(Paths.get(GameConfigManager.GAME_CONFIG_PATH, ZONE_CONFIG_SUB_PATH), zoneConfigs, allZoneConfigs);
        allZoneConfigs.sort(Comparator.comparingInt(ZoneConfig::getZoneId));
    }

    private void loadConfigsFromDirectory(Path directoryPath, Map<Integer, ZoneConfig> configMap, List<ZoneConfig> configList) {
        try (Stream<Path> pathStream = Files.list(directoryPath)) {
            pathStream.filter(path -> path.toString().endsWith(".json"))
                    .forEach(filePath -> loadConfigFromFile(filePath, configMap, configList));
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not list files in directory: {}", directoryPath, e);
        }
    }

    private void loadConfigFromFile(Path filePath, Map<Integer, ZoneConfig> configMap, List<ZoneConfig> configList) {
        try (InputStream inputStream = Files.newInputStream(filePath);
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            JsonArray configArray = gson.fromJson(reader, JsonArray.class);
            if (configArray == null) {
                BattleRoyale.LOGGER.info("Skipped empty configuration from {}", filePath);
                return;
            }
            for (JsonElement element : configArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject configObject = element.getAsJsonObject();
                try {
                    int zoneId = configObject.has(ZoneConfigTag.ZONE_ID) ? configObject.getAsJsonPrimitive(ZoneConfigTag.ZONE_ID).getAsInt() : -1;
                    JsonObject zoneFuncObject = configObject.has(ZoneConfigTag.ZONE_FUNC) ? configObject.getAsJsonObject(ZoneConfigTag.ZONE_FUNC) : null;
                    JsonObject zoneShapeObject = configObject.has(ZoneConfigTag.ZONE_SHAPE) ? configObject.getAsJsonObject(ZoneConfigTag.ZONE_SHAPE) : null;
                    if (zoneId < 0 || zoneFuncObject == null || zoneShapeObject == null) {
                        BattleRoyale.LOGGER.warn("Skipping invalid zone config in {}", filePath);
                        continue;
                    }
                    String zoneName = configObject.has(ZoneConfigTag.ZONE_NAME) ? configObject.getAsJsonPrimitive(ZoneConfigTag.ZONE_NAME).getAsString() : "";
                    String zoneColor = configObject.has(ZoneConfigTag.ZONE_COLOR) ? configObject.getAsJsonPrimitive(ZoneConfigTag.ZONE_COLOR).getAsString() : "#0000FF";
                    int zoneDelay = configObject.has(ZoneConfigTag.ZONE_DELAY) ? configObject.getAsJsonPrimitive(ZoneConfigTag.ZONE_DELAY).getAsInt() : 0;
                    int zoneTime = configObject.has(ZoneConfigTag.ZONE_TIME) ? configObject.getAsJsonPrimitive(ZoneConfigTag.ZONE_TIME).getAsInt() : 0;
                    IZoneFuncEntry zoneFuncEntry = JsonUtils.deserializeZoneFuncEntry(zoneFuncObject);
                    IZoneShapeEntry zoneShapeEntry = JsonUtils.deserializeZoneShapeEntry(zoneShapeObject);
                    if (zoneFuncEntry == null || zoneShapeEntry == null) {
                        BattleRoyale.LOGGER.error("Failed to deserialize zone entry for id: {} in {}", zoneId, filePath);
                    }
                    ZoneConfig zoneConfig = new ZoneConfig(zoneId, zoneName, zoneColor, zoneDelay, zoneTime, zoneFuncEntry, zoneShapeEntry);
                    configMap.put(zoneId, zoneConfig);
                    configList.add(zoneConfig);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.error("Error parsing zone config entry in {}: {}", filePath, e.getMessage());
                }
            }
            BattleRoyale.LOGGER.info("{} zones already loaded from {}.", configList.size(), filePath);
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to load configuration from {}: {}", filePath, e.getMessage());
        }
    }

    private void initializeDefaultConfigsIfEmpty() {
        Path zoneConfigPath = Paths.get(GameConfigManager.GAME_CONFIG_PATH, ZONE_CONFIG_SUB_PATH);
        try {
            if (!Files.exists(zoneConfigPath) || Files.list(zoneConfigPath).findAny().isEmpty()) {
                BattleRoyale.LOGGER.info("No zone configurations found in {}, generating default", zoneConfigPath);
                DefaultZoneConfigGenerator.generateDefaultZoneConfig();
                loadZoneConfigs();
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not check for zone configurations: {}", e.getMessage());
        }
    }

    public static class ZoneConfig {
        private final int zoneId;
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

        public IZoneFuncEntry getZoneFunc() {
            return zoneFuncEntry;
        }

        public IZoneShapeEntry getZoneShapeEntry() {
            return zoneShapeEntry;
        }
    }
}