package xiao.battleroyale.api.config.sub;

public interface IConfigSaveable {

    boolean saveAllConfigs();
    boolean saveConfigs();
    boolean saveConfigs(int folderId);

    boolean backupAllConfigs(String backupRoot);
    boolean backupConfigs(String backupRoot);
    boolean backupConfigs(String backupRoot, int folderId);
}
