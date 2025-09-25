package xiao.battleroyale.config;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSingleEntry;
import xiao.battleroyale.util.ClassUtils;

import java.nio.file.Path;
import java.util.Map;

public class ReloadConfigs {

    /**
     * 实现 IConfigLoadable.reloadConfigs 的核心逻辑
     * @param context 抽象配置管理器实例 (this)
     * @param folderId 文件夹 ID。
     * @return 是否成功加载配置。
     */
    public static <T extends IConfigSingleEntry> boolean reloadConfigs( // 读取子文件夹下所有文件数据
                                                                        AbstractConfigSubManager<T> context,
                                                                        int folderId) {
        // 在清除前获取当前使用的配置文件名称
        String fileNameString = context.getConfigFileName(folderId).string;
        context.clear(folderId);

        // 获取当前子文件夹下所有文件数据引用
        Path configDirPath = context.getConfigDirPath(folderId);

        // 读取当前子文件夹下所有文件
        LoadConfigs.loadAllConfigsFromDirectory(context, configDirPath, folderId);

        if (!context.hasConfigLoaded(folderId)) { // 没有文件或者文件无效
            context.initializeDefaultConfigsIfEmpty(folderId); // 写入默认文件之后再读一次
            LoadConfigs.loadAllConfigsFromDirectory(context, configDirPath, folderId);
            if (!context.hasConfigLoaded(folderId)) {
                BattleRoyale.LOGGER.error("Failed to load default configs after generation for type: {}", context.getFolderType(folderId));
                return false;
            }
        }

        Map<String, ClassUtils.ArrayMap<Integer, T>> fileConfigs = context.getConfigFolderData(folderId).fileConfigsByFileName;
        if (!fileConfigs.containsKey(fileNameString)) { // 之前的文件名不存在
            if (fileConfigs.isEmpty()) {
                BattleRoyale.LOGGER.warn("No config files loaded for type {}. Cannot switch to any file.", context.getFolderType(folderId));
                return false;
            }
            fileNameString = fileConfigs.keySet().iterator().next();
        }

        // 遍历每个配置文件
        for (Map.Entry<String, ClassUtils.ArrayMap<Integer, T>> entry : fileConfigs.entrySet()) {
            // 遍历文件内每个配置
            for (T configEntry : entry.getValue().asList()) {
                if (!configEntry.isDefaultSelect()) {
                    continue;
                }
                fileNameString = entry.getKey();
                if (context.switchConfigFile(fileNameString, folderId)) { // 先切换到配置再覆盖应用默认
                    configEntry.applyDefault();
                    BattleRoyale.LOGGER.info("Applied default config, fileName:{}, configId:{}, type:{}", fileNameString, configEntry.getConfigId(), configEntry.getType());
                    return true;
                } else {
                    BattleRoyale.LOGGER.error("Unexpected config file switch, fileNameString:{}, configEntryId:{}, type:{}", fileNameString, configEntry.getConfigId(), configEntry.getType());
                }
            }
        }
        BattleRoyale.LOGGER.info("No default {} applied", context.getFolderType(folderId));
        return context.switchConfigFile(fileNameString, folderId);
    }
}
