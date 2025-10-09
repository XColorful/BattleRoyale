package xiao.battleroyale.config;

import xiao.battleroyale.api.config.IConfigSubManager;

public class ConfigSwitchable {

    public static boolean switchConfigFile(AbstractConfigManager context, String subManagerNameKey) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.switchConfigFile();
        } else {
            return false;
        }
    }
    public static boolean switchConfigFile(AbstractConfigManager context, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.switchConfigFile(folderId);
        } else {
            return false;
        }
    }
    public static boolean switchConfigFile(AbstractConfigManager context, String subManagerNameKey, String fileName) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.switchConfigFile(fileName);
        } else {
            return false;
        }
    }
    public static boolean switchConfigFile(AbstractConfigManager context, String subManagerNameKey, int folderId, String fileName) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return subManager.switchConfigFile(folderId, fileName);
        } else {
            return false;
        }
    }
}
