package xiao.battleroyale.config;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSingleEntry;
import xiao.battleroyale.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwitchConfig {

    /**
     * 实现 IConfigSwitchable.switchConfigFile 的核心逻辑（切换下一个）
     * @param context 抽象配置管理器实例 (this)
     * @param folderId 文件夹 ID。
     * @return 是否成功切换。
     */
    public static <T extends IConfigSingleEntry> boolean switchConfigFile( // 切换下一个配置
                                                                           AbstractConfigSubManager<T> context,
                                                                           int folderId) {

        Map<String, ClassUtils.ArrayMap<Integer, T>> fileConfigs = context.getConfigFolderData(folderId).fileConfigsByFileName;
        List<String> fileNames = new ArrayList<>(fileConfigs.keySet());
        if (fileNames.isEmpty()) {
            return false;
        }
        fileNames.sort(String::compareTo);

        int currentIndex = fileNames.indexOf(context.getConfigFileName(folderId).string); // indexOf可能返回-1
        int nextIndex = (currentIndex + 1) % fileNames.size(); // isEmpty()已经保证不会对0取模
        String nextFileName = fileNames.get(nextIndex);
        return context.switchConfigFile(nextFileName, folderId);
    }

    /**
     * 实现 IConfigSwitchable.switchConfigFile 的核心逻辑（指定名称切换）
     * @param context 抽象配置管理器实例 (this)
     * @param fileName 指定的文件名。
     * @param folderId 文件夹 ID。
     * @return 是否成功切换。
     */
    public static <T extends IConfigSingleEntry> boolean switchConfigFile( // 指定文件名切换配置
                                                                           AbstractConfigSubManager<T> context,
                                                                           @NotNull String fileName,
                                                                           int folderId) {

        ClassUtils.ArrayMap<Integer, T> selectedFileConfigs = context.getConfigFolderData(folderId).fileConfigsByFileName.get(fileName);

        if (selectedFileConfigs != null) {
            context.getConfigFileName(folderId).string = fileName;
            context.getConfigFolderData(folderId).currentConfigs.putAll(selectedFileConfigs.asMap());
            BattleRoyale.LOGGER.debug("Switched to config file '{}' for type: {}", fileName, context.getFolderType(folderId));
            if (!selectedFileConfigs.isEmpty()) {
                T configEntry = selectedFileConfigs.listGet(0);
                configEntry.applyDefault();
                BattleRoyale.LOGGER.debug("Applied first config while switching config file, fileName:{}, configId:{}, type:{}", fileName, configEntry.getConfigId(), configEntry.getType());
            }
            return true;
        } else {
            BattleRoyale.LOGGER.warn("Config file '{}' not found for type {}", fileName, context.getFolderType(folderId));
            return false;
        }
    }
}
