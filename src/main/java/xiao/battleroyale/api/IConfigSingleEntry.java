package xiao.battleroyale.api;

public interface IConfigSingleEntry extends IConfigEntry {

    /**
     * 获取配置id
     * 用于对多个独立配置排序
     */
    int getConfigId();
}
