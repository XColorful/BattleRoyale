package xiao.battleroyale.config;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.config.FolderConfigData.ConfigFileName;
import xiao.battleroyale.util.ClassUtils.ArrayMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @param <T> 具体配置类型
 */
public abstract class AbstractConfigSubManager<T extends IConfigSingleEntry> implements IConfigSubManager<T> {

    protected final int DEFAULT_CONFIG_FOLDER = 0;
    protected final Map<Integer, FolderConfigData<T>> allFolderConfigData = new HashMap<>(); // folderId -> 文件夹下配置

    protected final String nameKey;
    public AbstractConfigSubManager(String nameKey) {
        this.nameKey = nameKey;
        allFolderConfigData.put(DEFAULT_CONFIG_FOLDER, new FolderConfigData<>());
    }

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

    /**
     * 供子类内部调用变量的getter
     * 没重载方法对folderId进行switch case就默认没有
     */
    // 获取指定文件夹下所有json文件数据
    protected Map<String, Map<Integer, T>> getFileConfigs() { return getFileConfigs(DEFAULT_CONFIG_FOLDER); }
    protected Map<String, Map<Integer, T>> getFileConfigs(int folderId) {
        Map<String, ArrayMap<Integer, T>> internalMaps = getConfigFolderData(folderId).fileConfigsByFileName;
        Map<String, Map<Integer, T>> externalView = new HashMap<>();
        for (Map.Entry<String, ArrayMap<Integer, T>> entry : internalMaps.entrySet()) {
            externalView.put(entry.getKey(), entry.getValue().asMap()); // 提供不可修改的Map视图
        }
        return externalView;
    }
    protected Map<String, List<T>> getFileConfigsList() { return getFileConfigsList(DEFAULT_CONFIG_FOLDER); }
    protected Map<String, List<T>> getFileConfigsList(int folderId) {
        Map<String, ArrayMap<Integer, T>> internalMaps = getConfigFolderData(folderId).fileConfigsByFileName;
        Map<String, List<T>> externalView = new HashMap<>();
        for (Map.Entry<String, ArrayMap<Integer, T>> entry : internalMaps.entrySet()) {
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
     * IManagerName
     */
    @Override public String getNameKey() {
        return this.nameKey;
    }

    /**
     * IConfigSubManager
     */
    @Override public @Nullable T getConfigEntry(int id) {
        return getConfigEntry(DEFAULT_CONFIG_FOLDER, id);
    }
    @Override public @Nullable T getConfigEntry(int folderId, int id) {
        return getConfigFolderData(folderId).currentConfigs.mapGet(id);
    }
    @Override public @Nullable  List<T> getConfigEntryList() {
        return getConfigEntryList(DEFAULT_CONFIG_FOLDER);
    }
    @Override public @Nullable List<T> getConfigEntryList(int folderId) {
        return getConfigFolderData(folderId).currentConfigs.asList();
    }
    @Override public String getCurrentSelectedFileName() {
        return getCurrentSelectedFileName(DEFAULT_CONFIG_FOLDER);
    }
    @Override public String getCurrentSelectedFileName(int folderId) {
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
    @Override public boolean reloadAllConfigs() {
        return SubReloadConfigs.reloadAllConfigs(this);
    }
    @Override public boolean reloadConfigs() {
        return reloadConfigs(DEFAULT_CONFIG_FOLDER);
    }
    @Override public boolean reloadConfigs(int folderId) { // 读取子文件夹下所有文件数据
        return SubReloadConfigs.reloadConfigs(this, folderId);
    }
    @Override public @Nullable T parseConfigEntry(JsonObject jsonObject, Path filePath) {
        return parseConfigEntry(jsonObject, filePath, DEFAULT_CONFIG_FOLDER);
    }
    @Override public boolean hasConfigLoaded() {
        return hasConfigLoaded(DEFAULT_CONFIG_FOLDER);
    }
    @Override public boolean hasConfigLoaded(int folderId) { // 仅判断是否已经读取到数据，不代表已经选中
        Map<String, ArrayMap<Integer, T>> fileConfigs = getConfigFolderData(folderId).fileConfigsByFileName;
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
    @Override public abstract String getConfigPath(int folderId);
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
        return SubSwitchConfig.switchConfigFile(this, folderId);
    }
    @Override
    public boolean switchConfigFile(String fileName) {
        return switchConfigFile(DEFAULT_CONFIG_FOLDER, fileName);
    }
    @Override
    public boolean switchConfigFile(int folderId, String fileName) { // 指定文件名切换配置
        return SubSwitchConfig.switchConfigFile(this, folderId, fileName);
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