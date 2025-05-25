package xiao.battleroyale.config.common.game.gamerule;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.IConfigManager;
import xiao.battleroyale.api.game.gamerule.GameruleConfigTag;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.defaultconfigs.DefaultGameruleConfigGenerator;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
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

public class GameruleConfigManager implements IConfigManager {
    public static final int DEFAULT_CONFIG_ID = 0;

    public static final String GAMERULE_CONFIG_SUB_PATH = "gamerule";

    private final Map<Integer, GameruleConfig> gameruleConfigs = new HashMap<>();
    private final List<GameruleConfig> allGameruleConfigs = new ArrayList<>();

    private static GameruleConfigManager instance;

    private GameruleConfigManager() {
        ;
    }

    public void reloadConfigs() {
        loadGameruleConfigs();

        initializeDefaultConfigsIfEmpty();
    }

    public static void init() {
        if (instance == null) {
            instance = new GameruleConfigManager();
            instance.reloadConfigs();
        }
    }

    public static GameruleConfigManager get() {
        if (instance == null) {
            GameruleConfigManager.init();
        }
        return instance;
    }

    public GameruleConfig getGameruleConfig(int gameId) {
        return gameruleConfigs.get(gameId);
    }

    public List<GameruleConfig> getAllGameruleConfigs() {
        return allGameruleConfigs;
    }

    public void loadGameruleConfigs() {
        gameruleConfigs.clear();
        allGameruleConfigs.clear();
        loadConfigsFromDirectory(Paths.get(GameConfigManager.GAME_CONFIG_PATH, GAMERULE_CONFIG_SUB_PATH), gameruleConfigs, allGameruleConfigs);
        allGameruleConfigs.sort(Comparator.comparingInt(GameruleConfig::getGameId));
    }

    private void loadConfigsFromDirectory(Path directoryPath, Map<Integer,GameruleConfig> configMap, List<GameruleConfig> configList) {
        try (Stream<Path> pathStream = Files.list(directoryPath)) {
            pathStream.filter(path -> path.toString().endsWith(".json"))
                    .forEach(filePath -> loadConfigFromFile(filePath, configMap, configList));
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not list files in directory: {}", directoryPath, e);
        }
    }

    private void loadConfigFromFile(Path filePath, Map<Integer, GameruleConfig> configMap, List<GameruleConfig> configList) {
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
                    int gameId = configObject.has(GameruleConfigTag.GAME_ID) ? configObject.getAsJsonPrimitive(GameruleConfigTag.GAME_ID).getAsInt() : -1;
                    JsonObject brEntryObject = configObject.has(GameruleConfigTag.BATTLEROYALE_ENTRY) ? configObject.getAsJsonObject(GameruleConfigTag.BATTLEROYALE_ENTRY) : null;
                    JsonObject mcEntryObject = configObject.has(GameruleConfigTag.MINECRAFT_ENTRY) ? configObject.getAsJsonObject(GameruleConfigTag.MINECRAFT_ENTRY) : null;
                    if (gameId < 0 || brEntryObject == null || mcEntryObject == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid gamerule config in {}", filePath);
                        continue;
                    }
                    String gameName = configObject.has(GameruleConfigTag.GAME_NAME) ? configObject.getAsJsonPrimitive(GameruleConfigTag.GAME_NAME).getAsString() : "";
                    String color = configObject.has(GameruleConfigTag.GAME_COLOR) ? configObject.getAsJsonPrimitive(GameruleConfigTag.GAME_COLOR).getAsString() : "";
                    BattleroyaleEntry brEntry = JsonUtils.deserializeBattleroyaleEntry(brEntryObject);
                    MinecraftEntry mcEntry = JsonUtils.deserializeMinecraftEntry(mcEntryObject);
                    if (brEntry == null || mcEntry == null) {
                        BattleRoyale.LOGGER.error("Failed to deserialize gamerule entry for id: {} in {}", gameId, filePath);
                        continue;
                    }
                    GameruleConfig gameruleConfig = new GameruleConfig(gameId, gameName, color, brEntry, mcEntry);
                    configMap.put(gameId, gameruleConfig);
                    configList.add(gameruleConfig);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.error("Error parsing gamerule config entry in {}: {}", filePath, e.getMessage());
                }
            }
            BattleRoyale.LOGGER.info("{} gamerule configurations already loaded from {}", configList.size(), filePath);
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to load configuration from {}: {}", filePath, e.getMessage());
        }
    }

    public void initializeDefaultConfigsIfEmpty() {
        if (!allGameruleConfigs.isEmpty()) {
            return;
        }
        BattleRoyale.LOGGER.info("No gamerule configurations loaded");
        Path gameruleConfigPath = Paths.get(GameConfigManager.GAME_CONFIG_PATH, GAMERULE_CONFIG_SUB_PATH);
        try {
            if (!Files.exists(gameruleConfigPath) || Files.list(gameruleConfigPath).findAny().isEmpty()) {
                BattleRoyale.LOGGER.info("No gamerule configurations found in {}, generating default", gameruleConfigPath);
            }
            DefaultGameruleConfigGenerator.generateDefaultGameruleConfigs();
            loadGameruleConfigs();
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not check for gamerule configurations: {}", e.getMessage());
        }
    }

    public static class GameruleConfig {
        private final int gameId;
        private final String gameName;
        private final String  color;
        private final BattleroyaleEntry brEntry;
        private final MinecraftEntry mcEntry;

        public GameruleConfig(int gameId, String name, String color, BattleroyaleEntry brEntry, MinecraftEntry mcEntry) {
            this.gameId = gameId;
            this.gameName = name;
            this.color = color;
            this.brEntry = brEntry;
            this.mcEntry = mcEntry;
        }

        public int getGameId() {
            return gameId;
        }

        public String getGameName() {
            return gameName;
        }

        public String getColor() {
            return color;
        }

        public BattleroyaleEntry getBattleRoyaleEntry() {
            return brEntry;
        }

        public MinecraftEntry getMinecraftEntry() {
            return mcEntry;
        }
    }
}