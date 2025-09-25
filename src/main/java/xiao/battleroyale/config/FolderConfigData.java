package xiao.battleroyale.config;

import xiao.battleroyale.api.config.IConfigSingleEntry;
import xiao.battleroyale.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

public class FolderConfigData<T extends IConfigSingleEntry> {

    public int DEFAULT_CONFIG_ID = 0;
    public final Map<String, ClassUtils.ArrayMap<Integer, T>> fileConfigsByFileName;
    public ClassUtils.ArrayMap<Integer, T> currentConfigs;
    public final ConfigFileName configFileName = new ConfigFileName();

    public FolderConfigData() {
        this.fileConfigsByFileName = new HashMap<>();
        this.currentConfigs = new ClassUtils.ArrayMap<>(IConfigSingleEntry::getConfigId);
    }

    public static class ConfigFileName {
        protected String string = "";
        protected boolean isEmpty() {
            return string.isEmpty();
        }
    }
}
