package xiao.battleroyale.config;

import com.google.gson.JsonArray;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class SubSaveConfigs {

    public static <T extends IConfigSingleEntry> boolean saveAllConfigs(AbstractConfigSubManager<T> context) {
        boolean hasSaved = false;
        for (int folderId : context.allFolderConfigData.keySet()) {
            hasSaved |= context.saveConfigs(folderId);
        }
        return hasSaved;
    }
    public static <T extends IConfigSingleEntry> boolean saveConfigs(AbstractConfigSubManager<T> context, int folderId) {
        String configDirPath = String.valueOf(context.getConfigDirPath());
        return writeConfigs(context, configDirPath, folderId);
    }

    public static <T extends IConfigSingleEntry> boolean backupAllConfigs(AbstractConfigSubManager<T> context, String backupRoot) {
        boolean hasBackuped = false;
        for (int folderId : context.allFolderConfigData.keySet()) {
            hasBackuped |= context.backupConfigs(backupRoot, folderId);
        }
        return hasBackuped;
    }
    public static <T extends IConfigSingleEntry> boolean backupConfigs(AbstractConfigSubManager<T> context, String backupRoot, int folderId) {
        String configDirPath = String.valueOf(Paths.get(backupRoot, String.valueOf(context.getConfigDirPath())));
        return writeConfigs(context, configDirPath, folderId);
    }

    public static <T extends IConfigSingleEntry> boolean writeConfigs(AbstractConfigSubManager<T> context, String folderPath, int folderId) {
        boolean hasWrited = false;
        Map<String, List<T>> fileConfigs = context.getFileConfigsList(folderId);
        JsonArray jsonArray = new JsonArray();
        // 遍历文件名
        for (Map.Entry<String, List<T>> entry : fileConfigs.entrySet()) {
            String filePath = String.valueOf(Paths.get(folderPath, String.format("%s.json", entry.getKey())));
            // 遍历单个文件
            for (T configEntry : entry.getValue()) {
                jsonArray.add(configEntry.toJson());
            }
            // 写入
            hasWrited |= writeJsonToFile(filePath, jsonArray);
        }
        return hasWrited;
    }
}
