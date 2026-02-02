package xiao.battleroyale.config;

import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.util.ClassUtils.ArrayMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderConfigData<T extends IConfigSingleEntry> {

    public final int folderId;
    public int DEFAULT_CONFIG_ID = 0;
    public final Map<String, ArrayMap<Integer, T>> fileConfigsByFileName;
    public ArrayMap<Integer, T> currentConfigs;
    public int lastAppliedConfigId = 0;
    public final ConfigFileName configFileName = new ConfigFileName();

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
    public String getConfigFileName() {
        return this.configFileName.string;
    }
    public void setConfigFileName(String fileName) {
        this.configFileName.string = fileName;
    }
    public void setLastAppliedConfigId(int configId) {
        this.lastAppliedConfigId = configId;
    }
    public int getLastAppliedConfigId() {
        return lastAppliedConfigId;
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
}
