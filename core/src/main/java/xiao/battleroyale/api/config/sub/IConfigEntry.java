package xiao.battleroyale.api.config.sub;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public interface IConfigEntry {

    /**
     * 获取当前 Entry 的类型，用于 JSON 反序列化
     * @return Entry 的类型名称
     */
    String getType();

    /**
     * 将当前 Entry 序列化为 JSON 对象，用于配置存储和编辑
     * @return 包含 Entry 配置的 JSON 对象
     */
    JsonObject toJson();

    @NotNull IConfigEntry copy();
}
