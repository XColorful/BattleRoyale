package xiao.battleroyale.config.client;

import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.client.display.DisplayConfigManager;
import xiao.battleroyale.config.client.display.DisplayConfigManager.DisplayConfig;
import xiao.battleroyale.config.client.render.RenderConfigManager;
import xiao.battleroyale.config.client.render.RenderConfigManager.RenderConfig;

import java.nio.file.Paths;
import java.util.List;

public class ClientConfigManager {

    public static final String CLIENT_CONFIG_SUB_PATH = "client";
    public static final String CLIENT_CONFIG_PATH = Paths.get(AbstractConfigManager.MOD_CONFIG_PATH).resolve(CLIENT_CONFIG_SUB_PATH).toString();

    private static class ClientConfigManagerHolder {
        private static final ClientConfigManager INSTANCE = new ClientConfigManager();
    }

    public static ClientConfigManager get() {
        return ClientConfigManagerHolder.INSTANCE;
    }

    public static void init() {
        get();
        RenderConfigManager.init();
        DisplayConfigManager.init();
    }

    /**
     * IConfigManager
     */
    public String getRenderConfigEntryFileName() {
        return RenderConfigManager.get().getConfigEntryFileName();
    }
    public String getDisplayConfigEntryFileName() {
        return DisplayConfigManager.get().getConfigEntryFileName();
    }

    /**
     * IConfigDefaultable
     */
    public void generateDefaultRenderConfigs() {
        RenderConfigManager.get().generateDefaultConfigs();
    }
    public void generateDefaultDisplayConfigs() {
        DisplayConfigManager.get().generateDefaultConfigs();
    }

    /**
     * IConfigLoadable
     */
    public String getRenderConfigPath() {
        return String.valueOf(RenderConfigManager.get().getConfigDirPath());
    }
    public String getDisplayConfigPath() {
        return String.valueOf(DisplayConfigManager.get().getConfigDirPath());
    }

    /**
     * 特定类别的获取接口
     */
    public RenderConfig getRenderConfig(int id) {
        return RenderConfigManager.get().getRenderConfig(id);
    }
    public List<RenderConfig> getRenderConfigList() {
        return RenderConfigManager.get().getRenderConfigList();
    }
    public DisplayConfig getDisplayConfig(int id) {
        return DisplayConfigManager.get().getDisplayConfig(id);
    }
    public List<DisplayConfig> getDisplayConfigList() {
        return DisplayConfigManager.get().getDisplayConfigList();
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadAllConfigs() {
        reloadRenderConfigs();
        reloadDisplayConfigs();
    }
    public void reloadRenderConfigs() {
        RenderConfigManager.get().reloadRenderConfigs();
    }
    public void reloadDisplayConfigs() {
        DisplayConfigManager.get().reloadDisplayConfigs();
    }

    public boolean switchNextRenderConfig() {
        return RenderConfigManager.get().switchConfigFile();
    }
    public boolean switchRenderConfig(String fileName) {
        return RenderConfigManager.get().switchConfigFile(fileName);
    }
    public boolean switchNextDisplayConfig() {
        return DisplayConfigManager.get().switchConfigFile();
    }
    public boolean switchDisplayConfig(String fileName) {
        return DisplayConfigManager.get().switchConfigFile(fileName);
    }

    public boolean applyRenderConfig(int id) {
        RenderConfig renderConfig = getRenderConfig(id);
        if (renderConfig == null) {
            return false;
        }
        renderConfig.applyDefault();
        return true;
    }
    public boolean applyDisplayConfig(int id) {
        DisplayConfig displayConfig = getDisplayConfig(id);
        if (displayConfig == null) {
            return false;
        }
        displayConfig.applyDefault();
        return true;
    }

    public String getRenderConfigName(int id) {
        RenderConfig renderConfig = getRenderConfig(id);
        return renderConfig != null ? renderConfig.name : "";
    }
    public String getDisplayConfigName(int id) {
        DisplayConfig displayConfig = getDisplayConfig(id);
        return displayConfig != null ? displayConfig.name : "";
    }
}
