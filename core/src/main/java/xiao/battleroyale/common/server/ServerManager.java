package xiao.battleroyale.common.server;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.server.IServerManager;
import xiao.battleroyale.api.server.IServerSubManager;
import xiao.battleroyale.api.server.performance.IPerformanceManager;
import xiao.battleroyale.api.server.profile.IProfileManager;
import xiao.battleroyale.api.server.utilitity.IUtilityManager;
import xiao.battleroyale.common.server.performance.PerformanceManager;
import xiao.battleroyale.common.server.profile.ProfileManager;
import xiao.battleroyale.common.server.utility.UtilityManager;

public class ServerManager implements IServerManager {

    private static class ServerManagerHolder {
        private static final ServerManager INSTANCE = new ServerManager();
    }

    public static ServerManager get() {
        return ServerManagerHolder.INSTANCE;
    }

    protected ServerManager() {
    }

    public static void init(McSide mcSide) {
        PerformanceManager.init(mcSide);
        ProfileManager.init(mcSide);
        UtilityManager.init(mcSide);
    }

    private @NotNull IPerformanceManager performanceManager = PerformanceManager.get();
    private @NotNull IProfileManager profileManager = ProfileManager.get();
    private @NotNull IUtilityManager utilityManager = UtilityManager.get();

    protected void registerNewManager(IServerSubManager previousManager, IServerSubManager newManager) {
        // ServerSubManager 目前没有事件注册机制，如果有需要在这里添加
        BattleRoyale.LOGGER.debug("Register new ServerSubManager {} to server manager", newManager.getClass().getSimpleName());
    }

    @Override public boolean setPerformanceManager(@NotNull IPerformanceManager performanceManager) {
        registerNewManager(this.performanceManager, performanceManager);
        this.performanceManager = performanceManager;
        return true;
    }
    @Override public boolean setProfileManager(@NotNull IProfileManager profileManager) {
        registerNewManager(this.profileManager, profileManager);
        this.profileManager = profileManager;
        return true;
    }
    @Override public boolean setUtilityManager(@NotNull IUtilityManager utilityManager) {
        registerNewManager(this.utilityManager, utilityManager);
        this.utilityManager = utilityManager;
        return true;
    }
    @Override public @NotNull IPerformanceManager getPerformanceManager() {
        return performanceManager;
    }
    @Override public @NotNull IProfileManager getProfileManager() {
        return profileManager;
    }
    @Override public @NotNull IUtilityManager getUtilityManager() {
        return utilityManager;
    }
}
