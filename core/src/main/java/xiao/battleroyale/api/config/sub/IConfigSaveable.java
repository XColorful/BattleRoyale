package xiao.battleroyale.api.config.sub;

public interface IConfigSaveable {

    boolean saveAllConfigs();
    boolean saveConfigs();
    boolean saveConfigs(int folderId);

    boolean backupAllConfigs();
    boolean backupAllConfigs(String backupRoot);
    boolean backupConfigs();
    boolean backupConfigs(String backupRoot);
    boolean backupConfigs(int folderId);
    boolean backupConfigs(String backupRoot, int folderId);
}
