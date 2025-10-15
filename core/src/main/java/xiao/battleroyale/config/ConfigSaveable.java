package xiao.battleroyale.config;

import xiao.battleroyale.api.config.IConfigSubManager;

public class ConfigSaveable {
    
    public static boolean saveAllConfigs(AbstractConfigManager context) {
        boolean hasSaved = false;
        for (IConfigSubManager<?> subManager : context.subManagers) {
            hasSaved |= subManager.saveAllConfigs();
        }
        return hasSaved;
    }
    public static boolean saveConfigs(AbstractConfigManager context, String subManagerNameKey) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.saveAllConfigs();
        } else {
            return false;
        }
    }
    public static boolean saveConfigs(AbstractConfigManager context, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.saveConfigs(folderId);
        } else {
            return false;
        }
    }
    public static boolean backupAllConfigs(AbstractConfigManager context, String backupRoot) {
        boolean hasBackuped = false;
        for (IConfigSubManager<?> subManager : context.subManagers) {
            hasBackuped |= subManager.backupAllConfigs(backupRoot);
        }
        return hasBackuped;
    }
    public static boolean backupConfigs(AbstractConfigManager context, String backupRoot, String subManagerNameKey) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.backupAllConfigs(backupRoot);
        } else {
            return false;
        }
    }
    public static boolean backupConfigs(AbstractConfigManager context, String backupRoot, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.backupConfigs(backupRoot, folderId);
        } else {
            return false;
        }
    }
}
