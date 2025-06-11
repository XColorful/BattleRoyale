package xiao.battleroyale.api;

public interface IConfigSwitchable {

    /**
     * 切换到下一个配置
     * 基于当前配置文件名在列表中的位置
     */
    boolean switchConfigFile();
    boolean switchConfigFile(int configType);

    /**
     * 直接切换到指定名称配置
     */
    boolean switchConfigFile(String fileName);
    boolean switchConfigFile(String fileName, int configType);
}
