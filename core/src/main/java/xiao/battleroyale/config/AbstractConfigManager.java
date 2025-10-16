package xiao.battleroyale.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.util.ClassUtils.ArrayMap;

import java.util.List;

public abstract class AbstractConfigManager implements IConfigManager {

    protected final String nameKey;
    public AbstractConfigManager(String nameKey) {
        this.nameKey = nameKey;
    }

    protected final ArrayMap<String, IConfigSubManager<?>> subManagers = new ArrayMap<>(IConfigSubManager::getNameKey);

    /**
     * IManagerName
     */
    @Override public String getNameKey() {
        return this.nameKey;
    }

    // IConfigManager
    @Override public boolean registerSubManager(IConfigSubManager<?> subManager) {
        if (this.subManagers.containsKey(subManager.getNameKey())) {
            return false;
        } else {
            this.subManagers.add(subManager);
            return true;
        }
    }
    @Override public boolean unregisterSubManager(IConfigSubManager<?> subManager) {
        if (this.subManagers.containsKey(subManager.getNameKey())) {
            this.subManagers.remove(subManager.getNameKey());
            return true;
        } else {
            return false;
        }
    }
    @Override public @Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey) {
        return this.subManagers.mapGet(subManagerNameKey);
    }
    @Override public List<IConfigSubManager<?>> getConfigSubManagers() {
        return this.subManagers.asList();
    }

    // IConfigSubManager
    @Override public @Nullable IConfigSingleEntry getConfigEntry(String subManagerNameKey, int id) {
        IConfigSubManager<?> subManager = this.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.getConfigEntry(id);
        } else {
            return null;
        }
    }
    @Override public @Nullable IConfigSingleEntry getConfigEntry(String subManagerNameKey, int folderId, int id) {
        IConfigSubManager<?> subManager = this.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.getConfigEntry(folderId, id);
        } else {
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    @Override public @Nullable List<IConfigSingleEntry> getConfigEntryList(String subManagerNameKey) {
        IConfigSubManager<?> subManager = this.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return (List<IConfigSingleEntry>) subManager.getConfigEntryList();
        } else {
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    @Override public @Nullable List<IConfigSingleEntry> getConfigEntryList(String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = this.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return (List<IConfigSingleEntry>) subManager.getConfigEntryList(folderId);
        } else {
            return null;
        }
    }
    @Override public @Nullable String getCurrentSelectedFileName(String subManagerNameKey) {
        IConfigSubManager<?> subManager = this.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.getCurrentSelectedFileName();
        } else {
            return null;
        }
    }
    @Override public @Nullable String getCurrentSelectedFileName(String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = this.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.getCurrentSelectedFileName(folderId);
        } else {
            return null;
        }
    }
    @Override public @Nullable String getFolderType(String subManagerNameKey) {
        IConfigSubManager<?> subManager = this.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.getFolderType();
        } else {
            return null;
        }
    }
    @Override public @Nullable String getFolderType(String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = this.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.getFolderType(folderId);
        } else {
            return null;
        }
    }
}
