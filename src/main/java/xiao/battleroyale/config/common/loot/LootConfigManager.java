package xiao.battleroyale.config.common.loot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.defaultconfigs.DefaultLootConfigGenerator;
import xiao.battleroyale.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LootConfigManager {

    public static final int DEFAULT_CONFIG_ID = 0;

    private static final String COMMON_LOOT_CONFIG_PATH = "config/battleroyale/loot";
    private static final String LOOT_SPAWNER_CONFIG_SUB_PATH = "loot_spawner";
    private static final String ENTITY_SPAWNER_CONFIG_SUB_PATH = "entity_spawner";
    private static final String AIRDROP_CONFIG_SUB_PATH = "airdrop";
    private static final String AIRDROP_SPECIAL_CONFIG_SUB_PATH = "airdrop_special";
    private static final String SECRET_ROOM_CONFIG_SUB_PATH = "secret_room";

    private final Map<Integer, LootConfig> lootSpawnerConfigs = new HashMap<>();
    private final List<LootConfig> allLootSpawnerConfigs = new ArrayList<>();
    private final Map<Integer, LootConfig> airdropConfigs = new HashMap<>();
    private final List<LootConfig> allAirdropConfigs = new ArrayList<>();
    private final Map<Integer, LootConfig> airdropSpecialConfigs = new HashMap<>();
    private final List<LootConfig> allAirdropSpecialConfigs = new ArrayList<>();
    private final Map<Integer, LootConfig> secretRoomConfigs = new HashMap<>();
    private final List<LootConfig> allSecretRoomConfigs = new ArrayList<>();
    private final Map<Integer, LootConfig> entitySpawnerConfigs = new HashMap<>();
    private final List<LootConfig> allEntitySpawnerConfigs = new ArrayList<>();

    private static LootConfigManager instance;

    private LootConfigManager() {
        reloadConfigs();
    }

    public void reloadConfigs() {
        lootSpawnerConfigs.clear();
        allLootSpawnerConfigs.clear();
        loadLootSpawnerConfigs();
        entitySpawnerConfigs.clear();
        allEntitySpawnerConfigs.clear();
        loadEntitySpawnerConfigs();
        airdropConfigs.clear();
        allAirdropConfigs.clear();
        loadAirdropConfigs();
        airdropSpecialConfigs.clear();
        allAirdropSpecialConfigs.clear();
        loadAirdropSpecialConfigs();
        secretRoomConfigs.clear();
        allSecretRoomConfigs.clear();
        loadSecretRoomConfigs();

        initializeDefaultConfigsIfEmpty();
    }

    public static void init() {
        if (instance == null) {
            instance = new LootConfigManager();
        }
    }

    public static LootConfigManager get() {
        if (instance == null) {
            LootConfigManager.init();
        }
        return instance;
    }

    public LootConfig getLootConfig(BlockEntity be, int id) {
        if (be instanceof LootSpawnerBlockEntity) {
            return getLootSpawnerConfig(id);
        } else if (be instanceof EntitySpawnerBlockEntity) {
            return getEntitySpawnerConfig(id);
        }
        BattleRoyale.LOGGER.warn("unsupported BlockEntity type");
        return null;
    }

    public LootConfig getLootSpawnerConfig(int id) {
        return lootSpawnerConfigs.get(id);
    }

    public List<LootConfig> getAllLootSpawnerConfigs() {
        return allLootSpawnerConfigs;
    }

    public LootConfig getAirdropConfig(int id) {
        return airdropConfigs.get(id);
    }

    public List<LootConfig> getAllAirdropConfigs() {
        return allAirdropConfigs;
    }

    public LootConfig getSpecialAirdropConfig(int id) {
        return airdropSpecialConfigs.get(id);
    }

    public List<LootConfig> getAllSpecialAirdropConfigs() {
        return allAirdropSpecialConfigs;
    }

    public LootConfig getSecretRoomConfig(int id) {
        return secretRoomConfigs.get(id);
    }

    public List<LootConfig> getAllSecretRoomConfigs() {
        return allSecretRoomConfigs;
    }

    public LootConfig getEntitySpawnerConfig(int id) {
        return entitySpawnerConfigs.get(id);
    }

    public List<LootConfig> getAllEntitySpawnerConfigs() {
        return allEntitySpawnerConfigs;
    }

    private void loadLootSpawnerConfigs() {
        loadConfigsFromDirectory(Paths.get(COMMON_LOOT_CONFIG_PATH, LOOT_SPAWNER_CONFIG_SUB_PATH), lootSpawnerConfigs, allLootSpawnerConfigs);
        allLootSpawnerConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadEntitySpawnerConfigs() {
        loadConfigsFromDirectory(Paths.get(COMMON_LOOT_CONFIG_PATH, ENTITY_SPAWNER_CONFIG_SUB_PATH), entitySpawnerConfigs, allEntitySpawnerConfigs);
        allEntitySpawnerConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadAirdropConfigs() {
        loadConfigsFromDirectory(Paths.get(COMMON_LOOT_CONFIG_PATH, AIRDROP_CONFIG_SUB_PATH), airdropConfigs, allAirdropConfigs);
        allAirdropConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadAirdropSpecialConfigs() {
        loadConfigsFromDirectory(Paths.get(COMMON_LOOT_CONFIG_PATH, AIRDROP_SPECIAL_CONFIG_SUB_PATH), airdropSpecialConfigs, allAirdropSpecialConfigs);
        allAirdropSpecialConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadSecretRoomConfigs() {
        loadConfigsFromDirectory(Paths.get(COMMON_LOOT_CONFIG_PATH, SECRET_ROOM_CONFIG_SUB_PATH), secretRoomConfigs, allSecretRoomConfigs);
        allSecretRoomConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadConfigsFromDirectory(Path directoryPath, Map<Integer, LootConfig> configMap, List<LootConfig> configList) {
        try (Stream<Path> pathStream = Files.list(directoryPath)) {
            pathStream.filter(path -> path.toString().endsWith(".json"))
                    .forEach(filePath -> loadConfigFromFile(filePath, configMap, configList));
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not list files in directory: {}", directoryPath, e);
        }
    }

    private void loadConfigFromFile(Path filePath, Map<Integer, LootConfig> configMap, List<LootConfig> configList) {
        try (InputStream inputStream = Files.newInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            JsonArray configArray = gson.fromJson(reader, JsonArray.class);
            if (configArray != null) {
                for (JsonElement element : configArray) {
                    if (element.isJsonObject()) {
                        JsonObject configObject = element.getAsJsonObject();
                        if (configObject.has("id") && configObject.has("entry")) {
                            try {
                                int id = configObject.getAsJsonPrimitive("id").getAsInt();
                                if (id < 0) {
                                    BattleRoyale.LOGGER.warn("Skipping invalid loot config with negative id: {} in {}", id, filePath);
                                    continue;
                                }
                                String name = configObject.has("name") ? configObject.getAsJsonPrimitive("name").getAsString() : "";
                                String color = configObject.has("color") ? configObject.getAsJsonPrimitive("color").getAsString() : "#FFFFFF";
                                JsonObject entryObject = configObject.getAsJsonObject("entry");
                                ILootEntry entry = JsonUtils.deserializeLootEntry(entryObject);
                                if (entry != null) {
                                    LootConfig lootConfig = new LootConfig(id, name, color, entry);
                                    configMap.put(id, lootConfig);
                                    configList.add(lootConfig);
                                } else {
                                    BattleRoyale.LOGGER.error("Failed to deserialize entry for id: {} in {}", id, filePath);
                                }
                            } catch (Exception e) {
                                BattleRoyale.LOGGER.error("Error parsing config entry in {}: {}", filePath, e.getMessage());
                            }
                        } else {
                            BattleRoyale.LOGGER.error("Invalid configuration entry: missing 'id' or 'entry' in {}", filePath);
                        }
                    }
                }
            }
            BattleRoyale.LOGGER.info("Loaded {} configurations from {}.", configList.size() - configMap.size() + configMap.size(), filePath);
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to load configuration from {}: {}", filePath, e.getMessage());
        }
    }

    private void initializeDefaultConfigsIfEmpty() {
        Path lootSpawnerPath = Paths.get(COMMON_LOOT_CONFIG_PATH, LOOT_SPAWNER_CONFIG_SUB_PATH);
        try {
            if (!Files.exists(lootSpawnerPath) || Files.list(lootSpawnerPath).findAny().isEmpty()) {
                BattleRoyale.LOGGER.info("No loot spawner configurations found in {}, generating default.", lootSpawnerPath);
                DefaultLootConfigGenerator.generateDefaultLootSpawnerConfig();
                loadLootSpawnerConfigs();
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not check for loot spawner configurations: {}", e.getMessage());
        }

        Path entitySpawnerPath = Paths.get(COMMON_LOOT_CONFIG_PATH, ENTITY_SPAWNER_CONFIG_SUB_PATH);
        try {
            if (!Files.exists(entitySpawnerPath) || Files.list(entitySpawnerPath).findAny().isEmpty()) {
                BattleRoyale.LOGGER.info("No entity spawner configurations found in {}, generating default.", entitySpawnerPath);
                DefaultLootConfigGenerator.generateDefaultEntitySpawnerConfig();
                loadEntitySpawnerConfigs();
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not check for entity spawner configurations: {}", e.getMessage());
        }

        Path airdropPath = Paths.get(COMMON_LOOT_CONFIG_PATH, AIRDROP_CONFIG_SUB_PATH);
        try {
            if (!Files.exists(airdropPath) || Files.list(airdropPath).findAny().isEmpty()) {
                BattleRoyale.LOGGER.info("No airdrop configurations found in {}, generating default.", airdropPath);
                DefaultLootConfigGenerator.generateDefaultAirdropConfig();
                loadAirdropConfigs();
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not check for airdrop configurations: {}", e.getMessage());
        }

        Path airdropSpecialPath = Paths.get(COMMON_LOOT_CONFIG_PATH, AIRDROP_SPECIAL_CONFIG_SUB_PATH);
        try {
            if (!Files.exists(airdropSpecialPath) || Files.list(airdropSpecialPath).findAny().isEmpty()) {
                BattleRoyale.LOGGER.info("No special airdrop configurations found in {}, generating default.", airdropSpecialPath);
                DefaultLootConfigGenerator.generateDefaultAirdropSpecialConfig();
                loadAirdropSpecialConfigs();
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not check for special airdrop configurations: {}", e.getMessage());
        }

        Path secretRoomPath = Paths.get(COMMON_LOOT_CONFIG_PATH, SECRET_ROOM_CONFIG_SUB_PATH);
        try {
            if (!Files.exists(secretRoomPath) || Files.list(secretRoomPath).findAny().isEmpty()) {
                BattleRoyale.LOGGER.info("No secret room configurations found in {}, generating default.", secretRoomPath);
                DefaultLootConfigGenerator.generateDefaultSecretRoomConfig();
                loadSecretRoomConfigs();
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not check for secret room configurations: {}", e.getMessage());
        }
    }

    public static class LootConfig {
        private final int id;
        private final String name;
        private final String color;
        private final ILootEntry entry;

        public LootConfig(int id, String name, String color, ILootEntry entry) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.entry = entry;
        }

        public int getId() {
            return id;
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
    }
}