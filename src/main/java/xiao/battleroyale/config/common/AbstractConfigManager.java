package xiao.battleroyale.config.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.IConfigEntry;
import xiao.battleroyale.api.IConfigManager;
import xiao.battleroyale.api.IConfigSingleEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @param <T> 具体配置类型
 */
public abstract class AbstractConfigManager<T extends IConfigSingleEntry> implements IConfigManager<T> {

    public static String MOD_CONFIG_PATH = "config/battleroyale";

    protected final int DEFAULT_CONFIG_DATA_ID = 0;
    protected final Map<Integer, ConfigData<T>> allConfigData = new HashMap<>();

    protected ConfigData<T> getConfigData() {
        return getConfigData(DEFAULT_CONFIG_DATA_ID);
    }
    protected final ConfigData<T> getConfigData(int dataId) {
        if (allConfigData.containsKey(dataId)) {
            return allConfigData.get(dataId);
        } else { // 默认不会触发，触发了也别崩溃
            BattleRoyale.LOGGER.error("Unexpected ConfigManager dataId {}, default config dir: {}", dataId, getConfigDirPath());
            return allConfigData.get(DEFAULT_CONFIG_DATA_ID);
        }
    }

    protected static class ConfigFileName {
        protected String string = "";
        protected boolean isEmpty() {
            return string.isEmpty();
        }
    }

    protected static class ConfigData<T extends IConfigEntry> {
        public int DEFAULT_CONFIG_ID = 0;
        public final Map<String, Map<Integer, T>> fileConfigs = new HashMap<>();
        public final Map<String, List<T>> allFileConfigs = new HashMap<>();
        public Map<Integer, T> configs = new HashMap<>();
        public List<T> allConfigs = new ArrayList<>();
        public final ConfigFileName configFileName = new ConfigFileName();

        public ConfigData() {
            ;
        }
    }

    public AbstractConfigManager() {
        allConfigData.put(DEFAULT_CONFIG_DATA_ID, new ConfigData<>()); // 手动添加一个0键，子类构造函数没写也不会崩溃
    }

    /**
     * 供子类内部调用变量的getter
     * 没重载方法对configType进行switch case就默认没有
     */
    protected Map<String, Map<Integer, T>> getFileConfigs() { return getFileConfigs(DEFAULT_CONFIG_DATA_ID); }
    protected Map<String, Map<Integer, T>> getFileConfigs(int configType) { return getConfigData(configType).fileConfigs; }
    protected Map<String, List<T>> getAllFileConfigs() { return getAllFileConfigs(DEFAULT_CONFIG_DATA_ID); }
    protected Map<String, List<T>> getAllFileConfigs(int configType) { return getConfigData(configType).allFileConfigs; }
    protected Map<Integer, T> getConfigs() { return getConfigs(DEFAULT_CONFIG_DATA_ID); }
    protected Map<Integer, T> getConfigs(int configType) { return getConfigData(configType).configs; }
    protected List<T> getAllConfigs() { return getAllConfigs(DEFAULT_CONFIG_DATA_ID); }
    protected List<T> getAllConfigs(int configType) { return getConfigData(configType).allConfigs; }
    protected ConfigFileName getConfigFileName() { return getConfigFileName(DEFAULT_CONFIG_DATA_ID); }
    protected ConfigFileName getConfigFileName(int configType) { return getConfigData(configType).configFileName; }

    protected Comparator<T> getConfigIdComparator() {
        return getConfigIdComparator(getDefaultConfigId());
    }
    protected abstract Comparator<T> getConfigIdComparator(int configType);

    protected void clear() {
        this.clear(getDefaultConfigId());
    }
    protected void clear(int configType) {
        getFileConfigs(configType).clear();
        getAllFileConfigs(configType).clear();
        getConfigs(configType).clear();
        getAllConfigs(configType).clear();
        getConfigFileName(configType).string = "";
    }

    /**
     * 从单个文件读取配置
     */
    protected void loadConfigFromFile(Path filePath, Map<Integer, T> newFileConfigs, List<T> newAllFileConfigs, int configType) {
        try (InputStream inputStream = Files.newInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

             Gson gson = new Gson();
             JsonArray configArray = gson.fromJson(reader, JsonArray.class);
             if (configArray == null) {
                 BattleRoyale.LOGGER.info("Skipped empty config from {} for type {}", filePath, getConfigType(configType));
                 return;
             }

             for (JsonElement element : configArray) {
                 if (!element.isJsonObject()) {
                     continue;
                 }

                 JsonObject configObject = element.getAsJsonObject();
                 try {
                     T config = parseConfigEntry(configObject, filePath, configType);
                     if (config == null) {
                         BattleRoyale.LOGGER.info("Skipped invalid config in {} for type {}", filePath, getConfigType(configType));
                         continue;
                     }
                     int configId = config.getConfigId();
                     if (newFileConfigs.containsKey(configId)) {
                         BattleRoyale.LOGGER.info("Config with the same id: {}, will overwrite in {} for type {}", configId, filePath, getConfigType(configType));
                     }
                     newFileConfigs.put(configId, config);
                     newAllFileConfigs.add(config);
                 } catch (Exception e) {
                     BattleRoyale.LOGGER.info("Error parsing config entry in {} for type {}: {}", filePath, getConfigType(configType), e.getMessage());
                 }
             }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to load config from {}: {} for type {}", filePath.getFileName(), e.getMessage(), getConfigType(configType), e);
        }
    }

    /**
     * 从目录读取所有文件
     */
    protected void loadAllConfigsFromDirectory(Path dirPath,
                                               Map<String, Map<Integer, T>> fileConfigs, Map<String, List<T>> allFileConfigs, int configType) {
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                BattleRoyale.LOGGER.info("Created config directory: {} for type {}", dirPath, getConfigType(configType));
                return; // 目录刚创建，里面没有文件
            }

            List<Path> jsonFiles = Files.list(dirPath)
                    .filter(path -> path.toString().endsWith(".json"))
                    .toList();
            if (jsonFiles.isEmpty()) {
                BattleRoyale.LOGGER.info("No {} config file found in directory: {} ", getConfigType(configType), dirPath);
                return;
            }

            for (Path filePath : jsonFiles) {
                String fileNameWithoutExtension = filePath.getFileName().toString().replace(".json", "");
                Map<Integer, T> newFileConfigs = new HashMap<>();
                List<T> newAllFileConfigs = new ArrayList<>();
                loadConfigFromFile(filePath, newFileConfigs, newAllFileConfigs, configType);
                if (!newFileConfigs.isEmpty()) {
                    fileConfigs.put(fileNameWithoutExtension, newFileConfigs);
                    newAllFileConfigs.sort(getConfigIdComparator(configType));
                    allFileConfigs.put(fileNameWithoutExtension, newAllFileConfigs);
                    BattleRoyale.LOGGER.info("Loaded {} {} config from file: {} for type {}", newFileConfigs.size(), getConfigSubPath(configType), filePath.getFileName(), getConfigType(configType));
                } else {
                    BattleRoyale.LOGGER.info("No valid config for type {} found in file: {}", getConfigType(configType), filePath.getFileName());
                }
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not list files in directory: {} for type {}", dirPath, getConfigType(configType), e);
        }
    }

    /**
     * IConfigManager
     */
    @Override public @Nullable T getConfigEntry(int id) {
        return getConfigEntry(id, getDefaultConfigId());
    }
    @Override public @Nullable T getConfigEntry(int id, int configType) {
        return getConfigs(configType).get(id);
    }
    @Override public @Nullable  List<T> getAllConfigEntries() {
        return getAllConfigEntries(getDefaultConfigId());
    }
    @Override public @Nullable List<T> getAllConfigEntries(int configType) {
        return Collections.unmodifiableList(getAllConfigs(configType));
    }
    @Override public String getConfigEntryFileName() {
        return getConfigEntryFileName(getDefaultConfigId());
    }
    @Override public String getConfigEntryFileName(int configType) {
        return getConfigFileName(configType).string;
    }
    @Override public String getConfigType() {
        return getConfigType(getDefaultConfigId());
    }
    /**
     * IConfigLoadable
     */
    @Override public Set<String> getAvailableConfigFileNames() {
        return getAvailableConfigFileNames(getDefaultConfigId());
    }
    @Override public Set<String> getAvailableConfigFileNames(int configType) {
        return getAllFileConfigs(configType).keySet();
    }
    @Override public void reloadConfigs() {
        reloadConfigs(getDefaultConfigId());
    }
    @Override public void reloadConfigs(int configType) {
        String fileNameString = getConfigFileName(configType).string; // 在清除前获取名称
        this.clear(configType);

        Path configDirPath = getConfigDirPath(configType);
        Map<String, Map<Integer, T>> fileConfigs = getFileConfigs(configType);
        Map<String, List<T>> allFileConfigs = getAllFileConfigs(configType);
        loadAllConfigsFromDirectory(configDirPath,
                fileConfigs, allFileConfigs, configType);

        // 如果之前设置的文件名仍然有效则保持选中
        if (!fileNameString.isEmpty() && fileConfigs.containsKey(fileNameString)) {
            switchConfigFile(fileNameString, configType);
        } else if (!fileConfigs.isEmpty()) { // hasConfigLoaded(configType)检查的是已经选中的
            String firstFileName = fileConfigs.keySet().iterator().next();
            switchConfigFile(firstFileName, configType);
        } else {
            initializeDefaultConfigsIfEmpty(configType);
        }
    }
    @Override public @Nullable T parseConfigEntry(JsonObject jsonObject, Path filePath) {
        return parseConfigEntry(jsonObject, filePath, getDefaultConfigId());
    }
    @Override public boolean hasConfigLoaded() {
        return hasConfigLoaded(getDefaultConfigId());
    }
    @Override public boolean hasConfigLoaded(int configType) { // 只关心已经选定的配置是否为空
        return (!getConfigs(configType).isEmpty()) && (!getAllConfigs(configType).isEmpty());
    }
    @Override public void initializeDefaultConfigsIfEmpty() {
        initializeDefaultConfigsIfEmpty(getDefaultConfigId());
    }
    @Override public void initializeDefaultConfigsIfEmpty(int configType) {
        if (hasConfigLoaded(configType)) {
            return;
        }
        generateDefaultConfigs(configType);
        Path configDirPath = getConfigDirPath(configType);
        BattleRoyale.LOGGER.info("Generating default configs in {}", configDirPath);
        loadAllConfigsFromDirectory(configDirPath,
                getFileConfigs(configType), getAllFileConfigs(configType), configType);
        if (hasConfigLoaded(configType)) {
            String firstFileName = getAllFileConfigs(configType).keySet().iterator().next();
            switchConfigFile(firstFileName, configType);
        } else {
            BattleRoyale.LOGGER.error("Failed to load default configs after generation for type: {}", getConfigType(configType));
        }
    }
    @Override  public String getConfigPath() {
        return getConfigPath(getDefaultConfigId());
    }
    @Override public String getConfigPath(int configType) {
        return MOD_CONFIG_PATH;
    }
    @Override public String getConfigSubPath() {
        return getConfigSubPath(getDefaultConfigId());
    }
    @Override public Path getConfigDirPath() {
        return getConfigDirPath(getDefaultConfigId());
    }
    @Override public Path getConfigDirPath(int configType) {
        return Paths.get(getConfigPath(configType)).resolve(getConfigSubPath(configType));
    }

    /**
     * IConfigSwitchable
     */
    @Override
    public boolean switchConfigFile() {
        return switchConfigFile(getDefaultConfigId());
    }
    @Override
    public boolean switchConfigFile(int configType) {
        if (!hasConfigLoaded(configType)) {
            return false;
        }
        List<String> fileNames = new ArrayList<>(getAllFileConfigs(configType).keySet());
        fileNames.sort(String::compareTo);

        int currentIndex = fileNames.indexOf(getConfigFileName(configType).string);
        int nextIndex = (currentIndex + 1) % fileNames.size();
        String nextFileName = fileNames.get(nextIndex);
        return switchConfigFile(nextFileName, configType);
    }
    @Override
    public boolean switchConfigFile(String fileName) {
        return switchConfigFile(fileName, getDefaultConfigId());
    }
    @Override
    public boolean switchConfigFile(String fileName, int configType) {
        Map<String, Map<Integer, T>> fileConfigs = getFileConfigs(configType);
        Map<String, List<T>> allFileConfigs = getAllFileConfigs(configType);

        Map<Integer, T> configs = getConfigs(configType);
        List<T> allConfigs = getAllConfigs(configType);
        ConfigFileName configFileName = getConfigFileName(configType);

        if (allFileConfigs.containsKey(fileName)) {
            configFileName.string = fileName;
            configs.clear();
            configs.putAll(fileConfigs.get(fileName));
            allConfigs.clear();
            allConfigs.addAll(allFileConfigs.get(fileName));
            BattleRoyale.LOGGER.info("Switched to config file '{}' for type: {}", fileName, getConfigType(configType));
            return true;
        } else {
            BattleRoyale.LOGGER.warn("Config file '{}' not found for type {}", fileName, getConfigType(configType));
            return false;
        }
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(getDefaultConfigId());
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_CONFIG_DATA_ID);
    }
    @Override public int getDefaultConfigId(int configType) {
        return getConfigData(configType).DEFAULT_CONFIG_ID;
    }
    @Override public void setDefaultConfigId(int id) {
        setDefaultConfigId(id, getDefaultConfigId());
    }
    @Override public void setDefaultConfigId(int id, int configType) {
        getConfigData(configType).DEFAULT_CONFIG_ID = id;
    }
    @Override public @Nullable T getDefaultConfig() {
        return getDefaultConfig(getDefaultConfigId());
    }
    @Override public @Nullable T getDefaultConfig(int configType) {
        return getConfigEntry(getDefaultConfigId(configType));
    }
}
