package xiao.battleroyale.config;

import xiao.battleroyale.api.config.IConfigSubManager;

public class ConfigSwitchable {

    public static boolean switchConfigFile(AbstractConfigManager context, String subManagerNameKey) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.switchConfigFile();
            return true;
        } else {
            return false;
        }
    }
    public static boolean switchConfigFile(AbstractConfigManager context, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.switchConfigFile(folderId);
            return true;
        } else {
            return false;
        }
    }
    public static boolean switchConfigFile(AbstractConfigManager context, String subManagerNameKey, String fileName) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.switchConfigFile(fileName);
            return true;
        } else {
            return false;
        }
    }
    public static boolean switchConfigFile(AbstractConfigManager context, String subManagerNameKey, int folderId, String fileName) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.switchConfigFile(folderId, fileName);
            return true;
        } else {
            return false;
        }
    }
}
