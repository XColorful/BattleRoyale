package xiao.battleroyale.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.IConfigManager;
import xiao.battleroyale.api.IConfigSingleEntry;
import xiao.battleroyale.util.ClassUtils;

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

    protected final int DEFAULT_CONFIG_FOLDER = 0;
    protected final Map<Integer, FolderConfigData<T>> allFolderConfigData = new HashMap<>(); // folderId -> 文件夹下配置

    /**
     * 获取特定子文件夹的数据
     * 包含文件夹下所有json文件数据
     */
    protected FolderConfigData<T> getConfigFolderData() {
        return getConfigFolderData(DEFAULT_CONFIG_FOLDER);
    }
    protected final FolderConfigData<T> getConfigFolderData(int folderId) {
        if (allFolderConfigData.containsKey(folderId)) {
            return allFolderConfigData.get(folderId);
        } else { // 默认不会触发，触发了也别崩溃
            BattleRoyale.LOGGER.error("Unexpected ConfigManager folderId {}, default config dir: {}, default folderId: {}", folderId, getConfigDirPath(), DEFAULT_CONFIG_FOLDER);
            return allFolderConfigData.get(DEFAULT_CONFIG_FOLDER);
        }
    }

    protected static class ConfigFileName {
        protected String string = "";
        protected boolean isEmpty() {
            return string.isEmpty();
        }
    }

    protected static class FolderConfigData<T extends IConfigSingleEntry> {
        public int DEFAULT_CONFIG_ID = 0;
        public final Map<String, ClassUtils.ArrayMap<Integer, T>> fileConfigsByFileName; // fileName -> .json
        public ClassUtils.ArrayMap<Integer, T> currentConfigs; // 当前json文件数据，id -> 单个配置
        public final ConfigFileName configFileName = new ConfigFileName();

        public FolderConfigData() {
            this.fileConfigsByFileName = new HashMap<>();
            this.currentConfigs = new ClassUtils.ArrayMap<>(IConfigSingleEntry::getConfigId); // keyExtractor
        }
    }

    public AbstractConfigManager() {
        allFolderConfigData.put(DEFAULT_CONFIG_FOLDER, new FolderConfigData<>());
    }

    /**
     * 供子类内部调用变量的getter
     * 没重载方法对folderId进行switch case就默认没有
     */
    // 获取指定文件夹下所有json文件数据
    protected Map<String, Map<Integer, T>> getFileConfigs() { return getFileConfigs(DEFAULT_CONFIG_FOLDER); }
    protected Map<String, Map<Integer, T>> getFileConfigs(int folderId) {
        Map<String, ClassUtils.ArrayMap<Integer, T>> internalMaps = getConfigFolderData(folderId).fileConfigsByFileName;
        Map<String, Map<Integer, T>> externalView = new HashMap<>();
        for (Map.Entry<String, ClassUtils.ArrayMap<Integer, T>> entry : internalMaps.entrySet()) {
            externalView.put(entry.getKey(), entry.getValue().asMap()); // 提供不可修改的Map视图
        }
        return externalView;
    }
    protected Map<String, List<T>> getFileConfigsList() { return getFileConfigsList(DEFAULT_CONFIG_FOLDER); }
    protected Map<String, List<T>> getFileConfigsList(int folderId) {
        Map<String, ClassUtils.ArrayMap<Integer, T>> internalMaps = getConfigFolderData(folderId).fileConfigsByFileName;
        Map<String, List<T>> externalView = new HashMap<>();
        for (Map.Entry<String, ClassUtils.ArrayMap<Integer, T>> entry : internalMaps.entrySet()) {
            externalView.put(entry.getKey(), entry.getValue().asList()); // 提供不可修改的List视图
        }
        return externalView;
    }

    // 获取指定文件夹下当前选中的json文件数据
    protected Map<Integer, T> getConfigs() { return getConfigs(DEFAULT_CONFIG_FOLDER); }
    protected Map<Integer, T> getConfigs(int folderId) { return getConfigFolderData(folderId).currentConfigs.asMap(); }
    protected List<T> getConfigsList() { return getConfigsList(DEFAULT_CONFIG_FOLDER); }
    protected List<T> getConfigsList(int folderId) { return getConfigFolderData(folderId).currentConfigs.asList(); }

    // 获取指定文件夹下当前选中的json文件名
    protected ConfigFileName getConfigFileName() { return getConfigFileName(DEFAULT_CONFIG_FOLDER); }
    protected ConfigFileName getConfigFileName(int folderId) { return getConfigFolderData(folderId).configFileName; }

    protected Comparator<T> getConfigIdComparator() {
        return getConfigIdComparator(DEFAULT_CONFIG_FOLDER);
    }
    protected abstract Comparator<T> getConfigIdComparator(int folderId);

    protected void clear() {
        this.clear(DEFAULT_CONFIG_FOLDER);
    }
    protected void clear(int folderId) {
        getConfigFolderData(folderId).fileConfigsByFileName.clear();
        getConfigFolderData(folderId).currentConfigs.clear();
        getConfigFileName(folderId).string = "";
    }

    /**
     * 从单个json文件数据读取配置
     */
    protected void loadConfigFromFile(Path filePath, ClassUtils.ArrayMap<Integer, T> newFileConfigs, int folderId) {
        try (InputStream inputStream = Files.newInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            JsonArray configArray = gson.fromJson(reader, JsonArray.class);
            if (configArray == null) {
                BattleRoyale.LOGGER.debug("Skipped empty config from {} for type {}", filePath, getFolderType(folderId));
                return;
            }

            for (JsonElement element : configArray) {
                if (!element.isJsonObject()) {
                    continue;
                }

                JsonObject configObject = element.getAsJsonObject();
                try {
                    T config = parseConfigEntry(configObject, filePath, folderId);
                    if (config == null) {
                        BattleRoyale.LOGGER.debug("Skipped invalid config in {} for type {}", filePath, getFolderType(folderId));
                        continue;
                    }
                    int configId = config.getConfigId();
                    if (newFileConfigs.containsKey(configId)) {
                        BattleRoyale.LOGGER.debug("Config with the same id: {}, will overwrite in {} for type {}", configId, filePath, getFolderType(folderId));
                    }
                    newFileConfigs.put(configId, config);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.debug("Error parsing config entry in {} for type {}: {}", filePath, getFolderType(folderId), e.getMessage());
                }
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to load config from {}: {} for type {}", filePath.getFileName(), e.getMessage(), getFolderType(folderId), e);
        }
    }

    /**
     * 从文件夹下读取所有json文件数据
     */
    protected void loadAllConfigsFromDirectory(Path dirPath, int folderId) {
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                BattleRoyale.LOGGER.debug("Created config directory: {} for type {}", dirPath, getFolderType(folderId));
                return; // 目录刚创建，里面没有文件
            }

            List<Path> jsonFiles = Files.list(dirPath)
                    .filter(path -> path.toString().endsWith(".json"))
                    .toList();
            if (jsonFiles.isEmpty()) {
                BattleRoyale.LOGGER.info("No {} config file found in directory: {} ", getFolderType(folderId), dirPath);
                return;
            }

            // 遍历文件夹下所有json文件
            getConfigFolderData(folderId).fileConfigsByFileName.clear();

            for (Path filePath : jsonFiles) {
                String fileNameNoExtension = filePath.getFileName().toString().replace(".json", "");
                ClassUtils.ArrayMap<Integer, T> newFileConfigs = new ClassUtils.ArrayMap<>(IConfigSingleEntry::getConfigId);
                // 读取单个json文件数据
                loadConfigFromFile(filePath, newFileConfigs, folderId);

                if (!newFileConfigs.isEmpty()) {
                    getConfigFolderData(folderId).fileConfigsByFileName.put(fileNameNoExtension, newFileConfigs); // 允许文件名如".json"
                    newFileConfigs.sort(getConfigIdComparator(folderId));

                    BattleRoyale.LOGGER.debug("Loaded {} {} config from file: {} for type {}", newFileConfigs.size(), getConfigSubPath(folderId), filePath.getFileName(), getFolderType(folderId));
                } else {
                    BattleRoyale.LOGGER.info("No valid config for type {} found in file: {}", getFolderType(folderId), filePath.getFileName());
                }
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not list files in directory: {} for type {}", dirPath, getFolderType(folderId), e);
        }
    }

    /**
     * IConfigManager
     */
    @Override public @Nullable T getConfigEntry(int id) {
        return getConfigEntry(id, DEFAULT_CONFIG_FOLDER);
    }
    @Override public @Nullable T getConfigEntry(int id, int folderId) {
        return getConfigFolderData(folderId).currentConfigs.mapGet(id);
    }
    @Override public @Nullable  List<T> getConfigEntryList() {
        return getConfigEntryList(DEFAULT_CONFIG_FOLDER);
    }
    @Override public @Nullable List<T> getConfigEntryList(int folderId) {
        return getConfigFolderData(folderId).currentConfigs.asList();
    }
    @Override public String getConfigEntryFileName() {
        return getConfigEntryFileName(DEFAULT_CONFIG_FOLDER);
    }
    @Override public String getConfigEntryFileName(int folderId) {
        return getConfigFileName(folderId).string;
    }
    @Override public String getFolderType() {
        return getFolderType(DEFAULT_CONFIG_FOLDER);
    }
    /**
     * IConfigLoadable
     */
    @Override public Set<String> getAvailableConfigFileNames() {
        return getAvailableConfigFileNames(DEFAULT_CONFIG_FOLDER);
    }
    @Override public Set<String> getAvailableConfigFileNames(int folderId) {
        return getConfigFolderData(folderId).fileConfigsByFileName.keySet();
    }
    @Override public boolean reloadConfigs() {
        return reloadConfigs(DEFAULT_CONFIG_FOLDER);
    }
    @Override public boolean reloadConfigs(int folderId) { // 读取子文件夹下所有文件数据
        String fileNameString = getConfigFileName(folderId).string; // 在清除前获取当前使用的配置文件名称
        this.clear(folderId);

        // 获取当前子文件夹下所有文件数据引用
        Path configDirPath = getConfigDirPath(folderId);

        // 读取当前子文件夹下所有文件
        loadAllConfigsFromDirectory(configDirPath, folderId);

        if (!hasConfigLoaded(folderId)) { // 没有文件或者文件无效
            initializeDefaultConfigsIfEmpty(folderId); // 写入默认文件之后再读一次
            loadAllConfigsFromDirectory(configDirPath, folderId);
            if (!hasConfigLoaded(folderId)) {
                BattleRoyale.LOGGER.error("Failed to load default configs after generation for type: {}", getFolderType(folderId));
                return false;
            }
        }

        if (!getConfigFolderData(folderId).fileConfigsByFileName.containsKey(fileNameString)) { // 之前的文件名不存在
            if (getConfigFolderData(folderId).fileConfigsByFileName.isEmpty()) {
                BattleRoyale.LOGGER.warn("No config files loaded for type {}. Cannot switch to any file.", getFolderType(folderId));
                return false;
            }
            fileNameString = getConfigFolderData(folderId).fileConfigsByFileName.keySet().iterator().next();
        }

        // 遍历每个配置文件
        for (Map.Entry<String, ClassUtils.ArrayMap<Integer, T>> entry : getConfigFolderData(folderId).fileConfigsByFileName.entrySet()) {
            // 遍历文件内每个配置
            for (T configEntry : entry.getValue().asList()) {
                if (!configEntry.isDefaultSelect()) {
                    continue;
                }
                fileNameString = entry.getKey();
                if (switchConfigFile(fileNameString, folderId)) { // 先切换到配置再应用默认
                    configEntry.applyDefault();
                    BattleRoyale.LOGGER.info("Applied default config, fileName:{}, configId:{}, type:{}", fileNameString, configEntry.getConfigId(), configEntry.getType());
                    return true;
                } else {
                    BattleRoyale.LOGGER.error("Unexpected config file switch, fileNameString:{}, configEntryId:{}, type:{}", fileNameString, configEntry.getConfigId(), configEntry.getType());
                }
            }
        }
        BattleRoyale.LOGGER.info("No default {} applied", getFolderType(folderId));
        return switchConfigFile(fileNameString, folderId);
    }
    @Override public @Nullable T parseConfigEntry(JsonObject jsonObject, Path filePath) {
        return parseConfigEntry(jsonObject, filePath, DEFAULT_CONFIG_FOLDER);
    }
    @Override public boolean hasConfigLoaded() {
        return hasConfigLoaded(DEFAULT_CONFIG_FOLDER);
    }
    @Override public boolean hasConfigLoaded(int folderId) { // 仅判断是否已经读取到数据，不代表已经选中
        Map<String, ClassUtils.ArrayMap<Integer, T>> fileConfigs = getConfigFolderData(folderId).fileConfigsByFileName;
        return !fileConfigs.isEmpty() && fileConfigs.values().stream().anyMatch(arrayMap -> !arrayMap.isEmpty());
    }
    @Override public void initializeDefaultConfigsIfEmpty() {
        initializeDefaultConfigsIfEmpty(DEFAULT_CONFIG_FOLDER);
    }
    @Override public void initializeDefaultConfigsIfEmpty(int folderId) {
        if (hasConfigLoaded(folderId)) { // 防御一下
            return;
        }
        generateDefaultConfigs(folderId);
        BattleRoyale.LOGGER.info("Generated default configs in {}", getConfigDirPath(folderId));
    }
    @Override  public String getConfigPath() {
        return getConfigPath(DEFAULT_CONFIG_FOLDER);
    }
    @Override public String getConfigPath(int folderId) {
        return MOD_CONFIG_PATH;
    }
    @Override public String getConfigSubPath() {
        return getConfigSubPath(DEFAULT_CONFIG_FOLDER);
    }
    @Override public Path getConfigDirPath() {
        return getConfigDirPath(DEFAULT_CONFIG_FOLDER);
    }
    @Override public Path getConfigDirPath(int folderId) {
        return Paths.get(getConfigPath(folderId)).resolve(getConfigSubPath(folderId));
    }

    /**
     * IConfigSwitchable
     */
    @Override
    public boolean switchConfigFile() {
        return switchConfigFile(DEFAULT_CONFIG_FOLDER);
    }
    @Override
    public boolean switchConfigFile(int folderId) { // 切换下一个配置
        List<String> fileNames = new ArrayList<>(getConfigFolderData(folderId).fileConfigsByFileName.keySet());
        if (fileNames.isEmpty()) {
            return false;
        }
        fileNames.sort(String::compareTo);

        int currentIndex = fileNames.indexOf(getConfigFileName(folderId).string); // indexOf可能返回-1
        int nextIndex = (currentIndex + 1) % fileNames.size(); // isEmpty()已经保证不会对0取模
        String nextFileName = fileNames.get(nextIndex);
        return switchConfigFile(nextFileName, folderId);
    }
    @Override
    public boolean switchConfigFile(String fileName) {
        return switchConfigFile(fileName, DEFAULT_CONFIG_FOLDER);
    }
    @Override
    public boolean switchConfigFile(@NotNull String fileName, int folderId) { // 指定文件名切换配置
        ClassUtils.ArrayMap<Integer, T> selectedFileConfigs = getConfigFolderData(folderId).fileConfigsByFileName.get(fileName);

        if (selectedFileConfigs != null) {
            getConfigFileName(folderId).string = fileName;
            getConfigFolderData(folderId).currentConfigs.putAll(selectedFileConfigs.asMap());
            BattleRoyale.LOGGER.debug("Switched to config file '{}' for type: {}", fileName, getFolderType(folderId));
            return true;
        } else {
            BattleRoyale.LOGGER.warn("Config file '{}' not found for type {}", fileName, getFolderType(folderId));
            return false;
        }
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_CONFIG_FOLDER);
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_CONFIG_FOLDER);
    }
    @Override public int getDefaultConfigId(int folderId) {
        return getConfigFolderData(folderId).DEFAULT_CONFIG_ID;
    }
    @Override public void setDefaultConfigId(int id) {
        setDefaultConfigId(id, DEFAULT_CONFIG_FOLDER);
    }
    @Override public void setDefaultConfigId(int id, int folderId) {
        getConfigFolderData(folderId).DEFAULT_CONFIG_ID = id;
    }
    @Override public @Nullable T getDefaultConfig() {
        return getDefaultConfig(DEFAULT_CONFIG_FOLDER);
    }
    @Override public @Nullable T getDefaultConfig(int folderId) {
        return getConfigEntry(getDefaultConfigId(folderId));
    }
}