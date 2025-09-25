package xiao.battleroyale.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSingleEntry;
import xiao.battleroyale.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LoadConfigs {

    /**
     * 从单个json文件数据读取配置
     * @param context 抽象配置管理器实例 (this)
     * @param filePath 当前读取的文件路径。
     * @param newFileConfigs 读取到的配置集合。
     * @param folderId 文件夹 ID。
     */
    public static <T extends IConfigSingleEntry> void loadConfigFromFile(
            AbstractConfigSubManager<T> context,
            Path filePath,
            ClassUtils.ArrayMap<Integer, T> newFileConfigs,
            int folderId) {

        try (InputStream inputStream = Files.newInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            JsonArray configArray = gson.fromJson(reader, JsonArray.class);
            String folderType = context.getFolderType(folderId);

            if (configArray == null) {
                BattleRoyale.LOGGER.debug("Skipped empty config from {} for type {}", filePath, folderType);
                return;
            }

            for (JsonElement element : configArray) {
                if (!element.isJsonObject()) continue;

                JsonObject configObject = element.getAsJsonObject();
                try {
                    T config = context.parseConfigEntry(configObject, filePath, folderId);
                    if (config == null) {
                        BattleRoyale.LOGGER.debug("Skipped invalid config in {} for type {}", filePath, folderType);
                        continue;
                    }
                    int configId = config.getConfigId();
                    if (newFileConfigs.containsKey(configId)) {
                        BattleRoyale.LOGGER.debug("Config with the same id: {}, will overwrite in {} for type {}", configId, filePath, folderType);
                    }
                    newFileConfigs.put(configId, config);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.debug("Error parsing config entry in {} for type {}: {}", filePath, folderType, e.getMessage());
                }
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Failed to load config from {}: {} for type {}", filePath.getFileName(), e.getMessage(), context.getFolderType(folderId), e);
        }
    }

    /**
     * 从文件夹下读取所有json文件数据
     * @param context 抽象配置管理器实例 (this)
     * @param dirPath 配置目录路径。
     * @param folderId 文件夹 ID。
     */
    public static <T extends IConfigSingleEntry> void loadAllConfigsFromDirectory(
            AbstractConfigSubManager<T> context,
            Path dirPath,
            int folderId) {

        try {
            String folderType = context.getFolderType(folderId);

            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                BattleRoyale.LOGGER.debug("Created config directory: {} for type {}", dirPath, folderType);
                return; // 目录刚创建，里面没有文件
            }

            List<Path> jsonFiles = Files.list(dirPath)
                    .filter(path -> path.toString().endsWith(".json"))
                    .toList();
            if (jsonFiles.isEmpty()) {
                BattleRoyale.LOGGER.info("No {} config file found in directory: {} ", folderType, dirPath);
                return;
            }

            // 遍历文件夹下所有json文件
            context.getConfigFolderData(folderId).fileConfigsByFileName.clear();

            for (Path filePath : jsonFiles) {
                String fileNameNoExtension = filePath.getFileName().toString().replace(".json", "");
                ClassUtils.ArrayMap<Integer, T> newFileConfigs = new ClassUtils.ArrayMap<>(IConfigSingleEntry::getConfigId);
                // 读取单个json文件数据
                loadConfigFromFile(context, filePath, newFileConfigs, folderId);

                if (!newFileConfigs.isEmpty()) {
                    context.getConfigFolderData(folderId).fileConfigsByFileName.put(fileNameNoExtension, newFileConfigs);
                    newFileConfigs.sort(context.getConfigIdComparator(folderId));

                    BattleRoyale.LOGGER.debug("Loaded {} {} config from file: {} for type {}", newFileConfigs.size(), context.getConfigSubPath(folderId), filePath.getFileName(), folderType);
                } else {
                    BattleRoyale.LOGGER.info("No valid config for type {} found in file: {}", folderType, filePath.getFileName());
                }
            }
        } catch (IOException e) {
            BattleRoyale.LOGGER.error("Could not list files in directory: {} for type {}", dirPath, context.getFolderType(folderId), e);
        }
    }
}
