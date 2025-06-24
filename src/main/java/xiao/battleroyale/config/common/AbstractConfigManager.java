package xiao.battleroyale.config.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
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

    protected final int DEFAULT_CONFIG_FOLDER = 0;
    protected final Map<Integer, FolderConfigData<T>> allFolderConfigData = new HashMap<>();

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
            BattleRoyale.LOGGER.error("Unexpected ConfigManager folderId {}, default config dir: {}", folderId, getConfigDirPath());
            return allFolderConfigData.get(DEFAULT_CONFIG_FOLDER);
        }
    }

    protected static class ConfigFileName {
        protected String string = "";
        protected boolean isEmpty() {
            return string.isEmpty();
        }
    }

    protected static class FolderConfigData<T extends IConfigEntry> {
        public int DEFAULT_CONFIG_ID = 0;
        // 文件夹下所有json文件数据
        // Map<Integer, T>内容与List<T>相同，只是用T.id作为键加速查找
        public final Map<String, Map<Integer, T>> fileConfigs = new HashMap<>(); // fileName -> .json
        public final Map<String, List<T>> fileConfigsList = new HashMap<>(); // fileName -> .json
        // 当前json文件数据
        public Map<Integer, T> configs = new HashMap<>();
        public List<T> configsList = new ArrayList<>();
        public final ConfigFileName configFileName = new ConfigFileName();

        public FolderConfigData() {
            ;
        }
    }

    public AbstractConfigManager() {
        allFolderConfigData.put(DEFAULT_CONFIG_FOLDER, new FolderConfigData<>()); // 手动添加一个0键，子类构造函数没写也不会崩溃
    }

    /**
     * 供子类内部调用变量的getter
     * 没重载方法对folderId进行switch case就默认没有
     */
    // 获取指定文件夹下所有json文件数据
    protected Map<String, Map<Integer, T>> getFileConfigs() { return getFileConfigs(DEFAULT_CONFIG_FOLDER); }
    protected Map<String, Map<Integer, T>> getFileConfigs(int folderId) { return getConfigFolderData(folderId).fileConfigs; }
    protected Map<String, List<T>> getFileConfigsList() { return getFileConfigsList(DEFAULT_CONFIG_FOLDER); }
    protected Map<String, List<T>> getFileConfigsList(int folderId) { return getConfigFolderData(folderId).fileConfigsList; }
    // 获取指定文件夹下当前选中的json文件数据
    protected Map<Integer, T> getConfigs() { return getConfigs(DEFAULT_CONFIG_FOLDER); }
    protected Map<Integer, T> getConfigs(int folderId) { return getConfigFolderData(folderId).configs; }
    protected List<T> getConfigsList() { return getConfigsList(DEFAULT_CONFIG_FOLDER); }
    protected List<T> getConfigsList(int folderId) { return getConfigFolderData(folderId).configsList; }
    // 获取指定文件夹下当前选中的json文件名
    protected ConfigFileName getConfigFileName() { return getConfigFileName(DEFAULT_CONFIG_FOLDER); }
    protected ConfigFileName getConfigFileName(int folderId) { return getConfigFolderData(folderId).configFileName; }

    protected Comparator<T> getConfigIdComparator() {
        return getConfigIdComparator(getDefaultConfigId());
    }
    protected abstract Comparator<T> getConfigIdComparator(int folderId);

    protected void clear() {
        this.clear(getDefaultConfigId());
    }
    protected void clear(int folderId) {
        getFileConfigs(folderId).clear();
        getFileConfigsList(folderId).clear();
        getConfigs(folderId).clear();
        getConfigsList(folderId).clear();
        getConfigFileName(folderId).string = "";
    }

    /**
     * 从单个json文件数据读取配置
     */
    protected void loadConfigFromFile(Path filePath, Map<Integer, T> newFileConfigs, List<T> newFileConfigsList, int folderId) {
        try (InputStream inputStream = Files.newInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

             Gson gson = new Gson();
             JsonArray configArray = gson.fromJson(reader, JsonArray.class);
             if (configArray == null) {
                 BattleRoyale.LOGGER.info("Skipped empty config from {} for type {}", filePath, getFolderType(folderId));
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
                         BattleRoyale.LOGGER.info("Skipped invalid config in {} for type {}", filePath, getFolderType(folderId));
                         continue;
                     }
                     int configId = config.getConfigId();
                     if (newFileConfigs.containsKey(configId)) {
                         BattleRoyale.LOGGER.info("Config with the same id: {}, will overwrite in {} for type {}", configId, filePath, getFolderType(folderId));
                     }
                     newFileConfigs.put(configId, config);
                     newFileConfigsList.add(config);
                 } catch (Exception e) {
                     BattleRoyale.LOGGER.info("Error parsing config entry in {} for type {}: {}", filePath, getFolderType(folderId), e.getMessage());
                 }
             }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to load config from {}: {} for type {}", filePath.getFileName(), e.getMessage(), getFolderType(folderId), e);
        }
    }

    /**
     * 从文件夹下读取所有json文件数据
     */
    protected void loadAllConfigsFromDirectory(Path dirPath,
                                               Map<String, Map<Integer, T>> fileConfigs, Map<String, List<T>> fileConfigsList, int folderId) {
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                BattleRoyale.LOGGER.info("Created config directory: {} for type {}", dirPath, getFolderType(folderId));
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
            for (Path filePath : jsonFiles) {
                String fileNameNoExtension = filePath.getFileName().toString().replace(".json", "");
                Map<Integer, T> newFileConfigs = new HashMap<>();
                List<T> newFileConfigsList = new ArrayList<>();
                // 读取单个json文件数据
                loadConfigFromFile(filePath, newFileConfigs, newFileConfigsList, folderId);

                if (!newFileConfigs.isEmpty()) {
                    fileConfigs.put(fileNameNoExtension, newFileConfigs); // 允许文件名如".json"
                    newFileConfigsList.sort(getConfigIdComparator(folderId)); fileConfigsList.put(fileNameNoExtension, newFileConfigsList);
                    BattleRoyale.LOGGER.info("Loaded {} {} config from file: {} for type {}", newFileConfigs.size(), getConfigSubPath(folderId), filePath.getFileName(), getFolderType(folderId));
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
        return getConfigEntry(id, getDefaultConfigId());
    }
    @Override public @Nullable T getConfigEntry(int id, int folderId) {
        return getConfigs(folderId).get(id);
    }
    @Override public @Nullable  List<T> getConfigEntryList() {
        return getConfigEntryList(getDefaultConfigId());
    }
    @Override public @Nullable List<T> getConfigEntryList(int folderId) {
        return Collections.unmodifiableList(getConfigsList(folderId));
    }
    @Override public String getConfigEntryFileName() {
        return getConfigEntryFileName(getDefaultConfigId());
    }
    @Override public String getConfigEntryFileName(int folderId) {
        return getConfigFileName(folderId).string;
    }
    @Override public String getFolderType() {
        return getFolderType(getDefaultConfigId());
    }
    /**
     * IConfigLoadable
     */
    @Override public Set<String> getAvailableConfigFileNames() {
        return getAvailableConfigFileNames(getDefaultConfigId());
    }
    @Override public Set<String> getAvailableConfigFileNames(int folderId) {
        return getFileConfigsList(folderId).keySet();
    }
    @Override public boolean reloadConfigs() {
        return reloadConfigs(getDefaultConfigId());
    }
    @Override public boolean reloadConfigs(int folderId) { // 读取子文件夹下所有文件数据
        String fileNameString = getConfigFileName(folderId).string; // 在清除前获取当前使用的配置文件名称
        this.clear(folderId);

        // 获取当前子文件夹下所有文件数据引用
        Path configDirPath = getConfigDirPath(folderId);
        Map<String, Map<Integer, T>> fileConfigs = getFileConfigs(folderId); // fileName -> .json
        Map<String, List<T>> allFileConfigs = getFileConfigsList(folderId); // fileName -> .json

        // 读取当前子文件夹下所有文件
        loadAllConfigsFromDirectory(configDirPath,
                fileConfigs, allFileConfigs, folderId);
        if (!hasConfigLoaded(folderId)) { // 没有文件或者文件无效
            initializeDefaultConfigsIfEmpty(folderId); // 写入默认文件之后再读一次
            loadAllConfigsFromDirectory(configDirPath,
                    fileConfigs, allFileConfigs, folderId);
            if (!hasConfigLoaded(folderId)) {
                BattleRoyale.LOGGER.error("Failed to load default configs after generation for type: {}", getFolderType(folderId));
                return false;
            }
        }

        if (!fileConfigs.containsKey(fileNameString)) { // 之前的文件名不存在
            fileNameString = fileConfigs.keySet().iterator().next();
            for (Map.Entry<String, List<T>> entry : allFileConfigs.entrySet()) {
                boolean foundDefault = false;
                for (T configEntry : entry.getValue()) {
                    if (configEntry.isDefaultSelect()) {
                        fileNameString = entry.getKey();
                        foundDefault = true;
                        break;
                    }
                }
                if (foundDefault) {
                    break;
                }
            }
        }
        return switchConfigFile(fileNameString, folderId);
    }
    @Override public @Nullable T parseConfigEntry(JsonObject jsonObject, Path filePath) {
        return parseConfigEntry(jsonObject, filePath, getDefaultConfigId());
    }
    @Override public boolean hasConfigLoaded() {
        return hasConfigLoaded(getDefaultConfigId());
    }
    @Override public boolean hasConfigLoaded(int folderId) { // 仅判断是否已经读取到数据，不代表已经选中
        return (!getFileConfigs(folderId).isEmpty()) && (!getFileConfigsList(folderId).isEmpty());
    }
    @Override public void initializeDefaultConfigsIfEmpty() {
        initializeDefaultConfigsIfEmpty(getDefaultConfigId());
    }
    @Override public void initializeDefaultConfigsIfEmpty(int folderId) {
        if (hasConfigLoaded(folderId)) { // 防御一下
            return;
        }
        generateDefaultConfigs(folderId);
        Path configDirPath = getConfigDirPath(folderId);
        BattleRoyale.LOGGER.info("Generating default configs in {}", configDirPath);
    }
    @Override  public String getConfigPath() {
        return getConfigPath(getDefaultConfigId());
    }
    @Override public String getConfigPath(int folderId) {
        return MOD_CONFIG_PATH;
    }
    @Override public String getConfigSubPath() {
        return getConfigSubPath(getDefaultConfigId());
    }
    @Override public Path getConfigDirPath() {
        return getConfigDirPath(getDefaultConfigId());
    }
    @Override public Path getConfigDirPath(int folderId) {
        return Paths.get(getConfigPath(folderId)).resolve(getConfigSubPath(folderId));
    }

    /**
     * IConfigSwitchable
     */
    @Override
    public boolean switchConfigFile() {
        return switchConfigFile(getDefaultConfigId());
    }
    @Override
    public boolean switchConfigFile(int folderId) {
        List<String> fileNames = new ArrayList<>(getFileConfigsList(folderId).keySet());
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
        return switchConfigFile(fileName, getDefaultConfigId());
    }
    @Override
    public boolean switchConfigFile(@NotNull String fileName, int folderId) {
        Map<String, Map<Integer, T>> fileConfigs = getFileConfigs(folderId);
        Map<String, List<T>> allFileConfigs = getFileConfigsList(folderId);

        Map<Integer, T> configs = getConfigs(folderId);
        List<T> allConfigs = getConfigsList(folderId);
        ConfigFileName configFileName = getConfigFileName(folderId);

        if (allFileConfigs.containsKey(fileName)) { // TreeSet默认不支持null，尽管没使用
            configFileName.string = fileName;
            configs.clear();
            configs.putAll(fileConfigs.get(fileName));
            allConfigs.clear();
            allConfigs.addAll(allFileConfigs.get(fileName));
            BattleRoyale.LOGGER.info("Switched to config file '{}' for type: {}", fileName, getFolderType(folderId));
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
        generateDefaultConfigs(getDefaultConfigId());
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_CONFIG_FOLDER);
    }
    @Override public int getDefaultConfigId(int folderId) {
        return getConfigFolderData(folderId).DEFAULT_CONFIG_ID;
    }
    @Override public void setDefaultConfigId(int id) {
        setDefaultConfigId(id, getDefaultConfigId());
    }
    @Override public void setDefaultConfigId(int id, int folderId) {
        getConfigFolderData(folderId).DEFAULT_CONFIG_ID = id;
    }
    @Override public @Nullable T getDefaultConfig() {
        return getDefaultConfig(getDefaultConfigId());
    }
    @Override public @Nullable T getDefaultConfig(int folderId) {
        return getConfigEntry(getDefaultConfigId(folderId));
    }
}
