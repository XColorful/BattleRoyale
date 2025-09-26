package xiao.battleroyale.config;

import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.ISideOnly;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.server.ServerConfigManager;

import java.util.List;

public class ModConfigManager implements IModConfigManager, ISideOnly {

    public static String MOD_CONFIG_PATH = "config/battleroyale";

    private static class ModConfigManagerHolder {
        private static final ModConfigManager INSTANCE = new ModConfigManager();
    }

    public static IModConfigManager getApi() {
        return ModConfigManagerHolder.INSTANCE;
    }

    private ModConfigManager() {}

    public static void init(Dist dist) {
        GameConfigManager.init(dist);
        LootConfigManager.init(dist);
        ServerConfigManager.init(dist);
        EffectConfigManager.init(dist);
        ClientConfigManager.init(dist);
    }

    private final ModConfigManagerData configData = new ModConfigManagerData();

    @Override public boolean registerConfigManager(IConfigManager manager) {
        return configData.addManager(manager);
    }
    @Override public boolean registerConfigSubManager(IConfigSubManager<?> subManager) {
        return configData.addManager(subManager);
    }
    @Override public boolean unregisterConfigManager(IConfigManager manager) {
        return configData.removeManager(manager);
    }
    @Override public boolean unregisterConfigSubManager(IConfigSubManager<?> subManager) {
        return configData.removeManager(subManager);
    }

    @Override public List<IConfigManager> getConfigManagers() {
        return configData.getConfigManagers();
    }
    @Override public List<IConfigSubManager<?>> getConfigSubManagers() {
        return configData.getConfigSubManagers();
    }
    @Override public @Nullable IConfigManager getConfigManager(String managerNameKey) {
        return configData.getConfigManager(managerNameKey);
    }
    @Override public @Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey) {
        return configData.getConfigSubManager(subManagerNameKey);
    }

    @Override public boolean reloadAllConfigs() {
        return reloadAllConfigManagers() || reloadAllConfigSubManagers();
    }
    @Override public boolean reloadAllConfigManagers() {
        boolean hasReloaded = false;
        for (IConfigManager configManager : getConfigManagers()) {
            hasReloaded |= configManager.reloadAllConfigs();
        }
        return hasReloaded;
    }
    @Override public boolean reloadAllConfigSubManagers() {
        boolean hasReloaded = false;
        for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {
            hasReloaded |= configSubManager.reloadAllConfigs();
        }
        return hasReloaded;
    }

    @Override public boolean generateAllDefaultConfigs() {
        boolean hasGenerated = false;
        for (IConfigManager configManager : getConfigManagers()) {
            hasGenerated |= configManager.generateAllDefaultConfigs();
        }
        for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {
            configSubManager.generateAllDefaultConfigs();
            hasGenerated = true;
        }
        return hasGenerated;
    }

    @Override public boolean clientSideOnly() {
        List<IConfigManager> configManagers = getConfigManagers();
        if (configManagers.isEmpty()) {
            return false;
        }
        for (IConfigManager configManager : configManagers) {
            if (!configManager.clientSideOnly()) {
                return false;
            }
        }
        return true;
    }
    @Override public boolean serverSideOnly() {
        List<IConfigSubManager<?>> configSubManagers = getConfigSubManagers();
        if (configSubManagers.isEmpty()) {
            return false;
        }
        for (IConfigSubManager<?> configSubManager : configSubManagers) {
            if (!configSubManager.serverSideOnly()) {
                return false;
            }
        }
        return true;
    }
}
