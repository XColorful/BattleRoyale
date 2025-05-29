package xiao.battleroyale.common.game.spawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.util.ChatUtils;

import java.util.List;
import java.util.UUID;

public class SpawnManager extends AbstractGameManager {

    private static SpawnManager instance;

    private Vec3 lobbyPos;
    private Vec3 lobbyDimension;
    private boolean lobbyMuteki = true;

    private SpawnManager() {
        ;
    }

    public static void init() {
        if (instance == null) {
            instance = new SpawnManager();
        }
    }

    @NotNull
    public static SpawnManager get() {
        if (instance == null) {
            SpawnManager.init();
        }
        return instance;
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        int spawnConfigId = GameManager.get().getSpawnConfigId();
        SpawnConfig spawnConfig = SpawnConfigManager.get().getSpawnConfig(spawnConfigId);
        if (spawnConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_spawn_config");
            return;
        }
        int gameId = GameManager.get().getGameruleConfigId();
        GameruleConfig gameruleConfig = GameruleConfigManager.get().getGameruleConfig(gameId);
        if (gameruleConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }

        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        this.lobbyPos = brEntry.lobbyCenterPos;
        this.lobbyDimension = brEntry.lobbyDimension;
        this.lobbyMuteki = brEntry.lobbyMuteki;

        if (lobbyPos == null || lobbyDimension == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        this.prepared = true;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }
        if (!this.prepared) {
            return;
        }

        List<GamePlayer> gamePlayerList = GameManager.get().getGamePlayers();
        for (GamePlayer gamePlayer : gamePlayerList) {
            UUID id = gamePlayer.getPlayerUUID();
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(id);
            if (player == null) {
                continue;
            }
            player.teleportTo(lobbyPos.x, lobbyPos.y, lobbyPos.z);
        }

        this.ready = true;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return false;
        }
        if (!this.ready) {
            return false;
        }

        return true;
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        if (serverLevel != null) {
            List<GamePlayer> gamePlayerList = GameManager.get().getGamePlayers();
            for (GamePlayer gamePlayer : gamePlayerList) {
                UUID id = gamePlayer.getPlayerUUID();
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(id);
                if (player == null) {
                    continue;
                }
                player.teleportTo(lobbyPos.x, lobbyPos.y, lobbyPos.z);
            }
        }

        this.prepared = false;
        this.ready = false;
    }
}
