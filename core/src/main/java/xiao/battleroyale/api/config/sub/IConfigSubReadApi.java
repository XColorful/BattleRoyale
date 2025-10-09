package xiao.battleroyale.api.config.sub;

import java.util.List;
import java.util.Map;

public interface IConfigSubReadApi<T> {

    // 文件名 -> (configId -> configEntry)
    Map<String, Map<Integer, T>> getFileConfigsMap();
    Map<String, Map<Integer, T>> getFileConfigsMap(int folderId);
    // 文件名 -> configEntry[]
    Map<String, List<T>> getFileConfigsList();
    Map<String, List<T>> getFileConfigsList(int folderId);
}
