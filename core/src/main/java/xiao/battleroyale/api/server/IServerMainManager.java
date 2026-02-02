package xiao.battleroyale.api.server;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.server.performance.IPerformanceManager;
import xiao.battleroyale.api.server.profile.IProfileManager;
import xiao.battleroyale.api.server.utilitity.IUtilityManager;

public interface IServerMainManager extends IServerSubManager {

    boolean setPerformanceManager(@NotNull IPerformanceManager performanceManager);
    boolean setProfileManager(@NotNull IProfileManager profileManager);
    boolean setUtilityManager(@NotNull IUtilityManager utilityManager);

    @NotNull IPerformanceManager getPerformanceManager();
    @NotNull IProfileManager getProfileManager();
    @NotNull IUtilityManager getUtilityManager();
}
