package xiao.battleroyale.api.config.sub;

import org.jetbrains.annotations.NotNull;

public interface IConfigSingleEntry extends IConfigEntry, IConfigAppliable {

    /**
     * 获取配置id
     * 用于对多个独立配置排序
     */
    int getConfigId();

    String getName();

    boolean isDefaultSelect();

    @Override
    @NotNull
    IConfigSingleEntry copy();
}
