package xiao.battleroyale.config.common.loot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.config.common.loot.defaultconfigs.DefaultLootConfigGenerator;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootConfigManager {

    private static final String COMMON_LOOT_CONFIG_PATH = "config/battleroyale/loot/%s.json";
    private static final String LOOT_SPAWNER_CONFIG_FILE = "loot_spawner";
    private static final String ENTITY_SPAWNER_CONFIG_FILE = "entity_spawner";
    private static final String AIRDROP_CONFIG_FILE = "airdrop";
    private static final String AIRDROP_SPECIAL_CONFIG_FILE = "airdrop_special";
    private static final String SECRET_ROOM_CONFIG_FILE = "secret_room";

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
        loadLootSpawnerConfigs();
        loadEntitySpawnerConfigs();
        loadAirdropConfigs();
        loadAirdropSpecialConfigs();
        loadSecretRoomConfigs();
        initializeDefaultConfigsIfEmpty();
    }

    public void reloadConfigs() {
        lootSpawnerConfigs.clear();
        allLootSpawnerConfigs.clear();
        entitySpawnerConfigs.clear();
        allEntitySpawnerConfigs.clear();
        airdropConfigs.clear();
        allAirdropConfigs.clear();
        airdropSpecialConfigs.clear();
        allAirdropSpecialConfigs.clear();
        secretRoomConfigs.clear();
        allSecretRoomConfigs.clear();
        loadLootSpawnerConfigs();
        loadEntitySpawnerConfigs();
        loadAirdropConfigs();
        loadAirdropSpecialConfigs();
        loadSecretRoomConfigs();
        initializeDefaultConfigsIfEmpty();
        BattleRoyale.LOGGER.info("Loot configurations reloaded via command.");
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
        loadConfigs(LOOT_SPAWNER_CONFIG_FILE, lootSpawnerConfigs, allLootSpawnerConfigs);
        allLootSpawnerConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadEntitySpawnerConfigs() {
        loadConfigs(ENTITY_SPAWNER_CONFIG_FILE, entitySpawnerConfigs, allEntitySpawnerConfigs);
        allEntitySpawnerConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadAirdropConfigs() {
        loadConfigs(AIRDROP_CONFIG_FILE, airdropConfigs, allAirdropConfigs);
        allAirdropConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadAirdropSpecialConfigs() {
        loadConfigs(AIRDROP_SPECIAL_CONFIG_FILE, airdropSpecialConfigs, allAirdropSpecialConfigs);
        allAirdropSpecialConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadSecretRoomConfigs() {
        loadConfigs(SECRET_ROOM_CONFIG_FILE, secretRoomConfigs, allSecretRoomConfigs);
        allSecretRoomConfigs.sort(Comparator.comparingInt(LootConfig::getId));
    }

    private void loadConfigs(String configName, Map<Integer, LootConfig> configMap, List<LootConfig> configList) {
        String path = String.format(COMMON_LOOT_CONFIG_PATH, configName);
        try (InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8)) {
            if (reader == null) {
                BattleRoyale.LOGGER.warn("Could not load configuration file: {}", path);
                return;
            }
            Gson gson = new Gson();
            JsonArray configArray = gson.fromJson(reader, JsonArray.class);
            if (configArray != null) {
                for (com.google.gson.JsonElement element : configArray) {
                    if (element.isJsonObject()) {
                        JsonObject configObject = element.getAsJsonObject();
                        if (configObject.has("id") && configObject.has("entry")) {
                            try {
                                int id = configObject.getAsJsonPrimitive("id").getAsInt();
                                if (id < 0) {
                                    BattleRoyale.LOGGER.warn("Skipping invalid loot config with negative id: {} in {}", id, path);
                                    continue;
                                }
                                String name = configObject.has("name") ? configObject.getAsJsonPrimitive("name").getAsString() : "";
                                String color = configObject.has("color") ? configObject.getAsJsonPrimitive("color").getAsString() : "#FFFFFF";
                                JsonObject entryObject = configObject.getAsJsonObject("entry");
                                ILootEntry<?> entry = JsonUtils.deserializeLootEntry(entryObject);
                                if (entry != null) {
                                    LootConfig lootConfig = new LootConfig(id, name, color, entry);
                                    configMap.put(id, lootConfig);
                                    configList.add(lootConfig);
                                } else {
                                    BattleRoyale.LOGGER.error("Failed to deserialize entry for id: {} in {}", id, path);
                                }
                            } catch (Exception e) {
                                BattleRoyale.LOGGER.error("Error parsing config entry in {}: {}", path, e.getMessage());
                            }
                        } else {
                            BattleRoyale.LOGGER.error("Invalid configuration entry: missing 'id' or 'entry' in {}", path);
                        }
                    }
                }
            }
            BattleRoyale.LOGGER.info("Loaded {} configurations from {}.", configList.size(), path);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to load configuration from {}: {}", path, e.getMessage());
        }
    }

    private void initializeDefaultConfigsIfEmpty() {
        if (lootSpawnerConfigs.isEmpty()) {
            BattleRoyale.LOGGER.info("No loot spawner configurations found, generating default.");
            DefaultLootConfigGenerator.generateDefaultLootSpawnerConfig();
            loadLootSpawnerConfigs();
        }
        if (entitySpawnerConfigs.isEmpty()) {
            BattleRoyale.LOGGER.info("No entity spawner configurations found, generating default.");
            DefaultLootConfigGenerator.generateDefaultEntitySpawnerConfig();
            loadEntitySpawnerConfigs();
        }
        if (airdropConfigs.isEmpty()) {
            BattleRoyale.LOGGER.info("No airdrop configurations found, generating default.");
            DefaultLootConfigGenerator.generateDefaultAirdropConfig();
            loadAirdropConfigs();
        }
        if (airdropSpecialConfigs.isEmpty()) {
            BattleRoyale.LOGGER.info("No special airdrop configurations found, generating default.");
            DefaultLootConfigGenerator.generateDefaultAirdropSpecialConfig();
            loadAirdropSpecialConfigs();
        }
        if (secretRoomConfigs.isEmpty()) {
            BattleRoyale.LOGGER.info("No secret room configurations found, generating default.");
            DefaultLootConfigGenerator.generateDefaultSecretRoomConfig();
            loadSecretRoomConfigs();
        }
    }

    public static class LootConfig {
        private final int id;
        private final String name;
        private final String color;
        private final ILootEntry<?> entry;

        public LootConfig(int id, String name, String color, ILootEntry<?> entry) {
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

        public ILootEntry<?> getEntry() {
            return entry;
        }
    }
}