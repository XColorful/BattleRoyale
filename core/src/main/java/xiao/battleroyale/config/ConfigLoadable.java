package xiao.battleroyale.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.IConfigSubManager;

public class ConfigLoadable {

    public static boolean reloadAllConfigs(AbstractConfigManager context) {
        for (IConfigSubManager<?> subManager : context.subManagers) {
            subManager.reloadConfigs();
        }
        return !context.subManagers.isEmpty();
    }
    public static boolean reloadConfigs(AbstractConfigManager context, String subManagerNameKey) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.reloadConfigs();
            return true;
        } else {
            return false;
        }
    }
    public static boolean reloadConfigs(AbstractConfigManager context, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            subManager.reloadConfigs(folderId);
            return true;
        } else {
            return false;
        }
    }
    public static @Nullable String getConfigDirPath(AbstractConfigManager context, String subManagerNameKey) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return String.valueOf(subManager.getConfigDirPath());
        } else {
            return null;
        }
    }
    public static @Nullable String getConfigDirPath(AbstractConfigManager context, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = context.subManagers.mapGet(subManagerNameKey);
        if (subManager != null) {
            return String.valueOf(subManager.getConfigDirPath(folderId));
        } else {
            return null;
        }
    }
}
