package xiao.battleroyale.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.util.ClassUtils.ArrayMap;

import java.util.*;

public class FolderConfigData<T extends IConfigSingleEntry> {

    private final int folderId;
    private int DEFAULT_CONFIG_ID = 0;
    private final Map<String, ArrayMap<Integer, T>> fileConfigsByFileName;
    private final ArrayMap<Integer, T> currentConfigs;
    private int lastAppliedConfigId = 0;
    private final ConfigFileName configFileName = new ConfigFileName();

    @Deprecated
    public FolderConfigData() {
        this(0);
    }
    public FolderConfigData(int folderId) {
        this.folderId = folderId;
        this.fileConfigsByFileName = new HashMap<>();
        this.currentConfigs = new ArrayMap<>(IConfigSingleEntry::getConfigId);
    }

    public static class ConfigFileName {
        private String string = "";
        public boolean isEmpty() {
            return string.isEmpty();
        }
    }

    public int getFolderId() {
        return this.folderId;
    }
    public int getDefaultConfigId() {
        return DEFAULT_CONFIG_ID;
    }
    public void setDefaultConfigId(int defaultConfigId) {
        DEFAULT_CONFIG_ID = defaultConfigId;
    }
    public String getConfigFileName() {
        return this.configFileName.string;
    }
    public void setConfigFileName(String fileName) {
        this.configFileName.string = fileName;
    }
    public T getCurrentConfigEntry(int id) {
        return this.currentConfigs.mapGet(id);
    }
    public List<T> getCurrentConfigEntryList() {
        return this.currentConfigs.asList();
    }
    public ArrayMap<Integer, T> getConfigFile(String fileNameNoExtension) {
        return this.fileConfigsByFileName.get(fileNameNoExtension);
    }
    public void setLastAppliedConfigId(int configId) {
        this.lastAppliedConfigId = configId;
    }
    public Set<String> getConfigFileNames() {
        return fileConfigsByFileName.keySet();
    }
    public int getLastAppliedConfigId() {
        return lastAppliedConfigId;
    }
    public boolean hasConfigLoaded() {
        return !this.fileConfigsByFileName.isEmpty() && this.fileConfigsByFileName.values().stream().anyMatch(arrayMap -> !arrayMap.isEmpty());
    }

    public Map<String, List<T>> getFileConfigsList() {
        Map<String, List<T>> fileConfigs = new HashMap<>();
        for (Map.Entry<String, ArrayMap<Integer, T>> entry : fileConfigsByFileName.entrySet()) {
            fileConfigs.put(entry.getKey(), entry.getValue().asList());
        }
        return fileConfigs;
    }
    public Map<String, Map<Integer, T>> getFileConfigsMap() {
        Map<String, Map<Integer, T>> fileConfigs = new HashMap<>();
        for (Map.Entry<String, ArrayMap<Integer, T>> entry : fileConfigsByFileName.entrySet()) {
            fileConfigs.put(entry.getKey(), entry.getValue().asMap());
        }
        return fileConfigs;
    }

    public void clearAll() {
        this.fileConfigsByFileName.clear();
        this.currentConfigs.clear();
        setConfigFileName("");
    }
    public @Nullable ArrayMap<Integer, T> switchAndGetConfigFile(String fileNameNoExtension) {
        ArrayMap<Integer, T> fileConfigs = this.fileConfigsByFileName.get(fileNameNoExtension);
        if (fileConfigs == null) return null;

        this.currentConfigs.clearAndPutAll(fileConfigs.asMap());
        this.setConfigFileName(fileNameNoExtension);
        return fileConfigs;
    }

    public void putConfigFile(String fileNameNoExtension, ArrayMap<Integer, T> newFileConfigs) {
        this.fileConfigsByFileName.put(fileNameNoExtension, newFileConfigs);
    }

    public Map<String, ArrayMap<Integer, T>> getFileConfigsUnsafe() {
        return this.fileConfigsByFileName;
    }
}
