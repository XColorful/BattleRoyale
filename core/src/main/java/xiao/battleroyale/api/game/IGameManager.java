package xiao.battleroyale.api.game;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.lobby.IGameLobbyReadApi;
import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.api.game.zone.IGameZoneReadApi;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * GameManager单例专用
 */
public interface IGameManager extends IGameMainManager, IGameSubManager, IGameConfigGetter, IGameConfigSetter, IGameApiGetter, IGameEventReceiver {

    int getGameTime();
    UUID getGameId();
    boolean isInGame();
    Vec3 getGlobalCenterOffset();
    int getMaxGameTime();
    int getWinnerTeamTotal();
    int getRequiredGameTeam();
    ServerLevel getServerLevel();
    ResourceKey<Level> getGameLevelKey();
    Supplier<Float> getRandom();

    boolean setGameStep(int step);
    boolean setGlobalCenterOffset(Vec3 offset);
    void setDefaultLevel(String defaultLevelKey);

    void sendGameSpectateMessage(@NotNull ServerPlayer player);
    default boolean teleportToLobby(@NotNull LivingEntity livingEntity) {
        return getGameLobbyManager().teleportToLobby(livingEntity);
    }
    default boolean spectateGame(ServerPlayer player) {
        return getGameProcessManager().spectateGame(player);
    }

    void finishGame(boolean hasWinner);

    @Override default IGameTeamReadApi getGameTeamReadApi() {
        return getTeamManager();
    }
    @Override default IGameZoneReadApi getGameZoneReadApi() {
        return getZoneManager();
    }
    @Override default IGameLobbyReadApi getGameLobbyReadApi() {
        return getGameLobbyManager();
    }

    void onServerStopping();
    boolean isOnServerStopping();

    default void checkIfGameShouldEnd() {
        getGameProcessManager().checkIfGameShouldEnd();
    }

    @ApiStatus.Internal
    void addGameTimeAndTick();
    @Deprecated
    void setGameTime(int gameTime);
    @ApiStatus.Internal
    void setGameId(UUID gameId);
}
