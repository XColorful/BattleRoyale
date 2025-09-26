package xiao.battleroyale.config;

import xiao.battleroyale.api.config.IConfigSubManager;

public class ConfigDefaultable {
    
    public static boolean generateAllDefaultConfigs(AbstractConfigManager context) {
        for (IConfigSubManager<?> subManager : context.subManagers) {
            subManager.generateAllDefaultConfigs();
        }
        return !context.subManagers.isEmpty();
    }
    public static boolean generateDefaultConfig(AbstractConfigManager context, String subManagerNameKey) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.generateDefaultConfigs();
            return true;
        } else {
            return false;
        }
    }
    public static boolean generateDefaultConfig(AbstractConfigManager context, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.generateDefaultConfigs(folderId);
            return true;
        } else {
            return false;
        }
    }
    public static int getDefaultConfigId(AbstractConfigManager context, String subManagerNameKey) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.getDefaultConfigId();
        } else {
            return -1;
        }
    }
    public static int getDefaultConfigId(AbstractConfigManager context, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.getDefaultConfigId(folderId);
        } else {
            return -1;
        }
    }
    public static boolean setDefaultConfigId(AbstractConfigManager context, String subManagerNameKey, int id) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.setDefaultConfigId(id);
            return true;
        } else {
            return false;
        }
    }
    public static boolean setDefaultConfigId(AbstractConfigManager context, String subManagerNameKey, int id, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.setDefaultConfigId(id, folderId);
            return true;
        } else {
            return false;
        }
    }
}
