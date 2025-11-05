package xiao.battleroyale.api.game;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.spawn.IGameLobbyReadApi;
import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.api.game.zone.IGameZoneReadApi;

import java.util.UUID;

/**
 * GameManager单例专用
 */
public interface IGameManager extends IGameMainManager, IGameSubManager, IGameConfigGetter, IGameApiGetter {

    int getGameTime();
    UUID getGameId();
    boolean isInGame();
    Vec3 getGlobalCenterOffset();
    int getMaxGameTime();
    int getWinnerTeamTotal();
    int getRequiredGameTeam();
    ServerLevel getServerLevel();
    ResourceKey<Level> getGameLevelKey();

    boolean setGlobalCenterOffset(Vec3 offset);

    void sendGameSpectateMessage(@NotNull ServerPlayer player);
    void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner);
    boolean teleportToLobby(@NotNull LivingEntity livingEntity);
    boolean spectateGame(ServerPlayer player);

    void finishGame(boolean hasWinner);

    @Override default IGameTeamReadApi getGameTeamReadApi() {
        return getTeamManager();
    }
    @Override default IGameZoneReadApi getGameZoneReadApi() {
        return getZoneManager();
    }
    @Override default IGameLobbyReadApi getGameLobbyReadApi() {
        return getSpawnManager();
    }
}
