package xiao.battleroyale.common.game.spawn;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.event.game.LobbyEventHandler;
import xiao.battleroyale.util.ChatUtils;

import java.util.List;
import java.util.UUID;

public class SpawnManager extends AbstractGameManager {

    private static class SpawnManagerHolder {
        private static final SpawnManager INSTANCE = new SpawnManager();
    }

    public static SpawnManager get() {
        return SpawnManagerHolder.INSTANCE;
    }

    private SpawnManager() {}

    public static void init() {
        ;
    }

    private static SpawnManager instance;

    private Vec3 lobbyPos;
    private Vec3 lobbyDimension;
    private boolean lobbyMuteki = true;
    private IGameSpawner gameSpawner;

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        int spawnConfigId = GameManager.get().getSpawnConfigId();
        SpawnConfig spawnConfig = GameConfigManager.get().getSpawnConfig(spawnConfigId);
        if (spawnConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_spawn_config");
            return;
        }
        this.gameSpawner = spawnConfig.createGameSpawner();
        if (this.gameSpawner == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_spawn_config");
            return;
        }

        int gameId = GameManager.get().getGameruleConfigId();
        GameruleConfig gameruleConfig = GameConfigManager.get().getGameruleConfig(gameId);
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

        // Hyper Muteki的大厅
        if (this.lobbyMuteki) {
            LobbyEventHandler.register();
        } else {
            LobbyEventHandler.unregister();
        }
        this.prepared = true;
        BattleRoyale.LOGGER.debug("SpawnManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }
        if (!this.prepared) {
            return;
        }

        // 传送至大厅
        List<GamePlayer> gamePlayerList = GameManager.get().getGamePlayers();
        for (GamePlayer gamePlayer : gamePlayerList) {
            teleportGamePlayerToLobby(gamePlayer, serverLevel);
        }

        this.gameSpawner.clear();
        this.gameSpawner.init(GameManager.get().getRandom(), GameManager.get().getPlayerLimit()); // 用玩家上限作为点位数量
        if (!isReady()) {
            return;
        }
        BattleRoyale.LOGGER.debug("SpawnManager complete initGame");
    }

    @Override
    public boolean isReady() {
        return this.gameSpawner.isReady();
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return false;
        }

        return isReady();
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        LobbyEventHandler.unregister();
        this.prepared = false;
        // this.ready = false; // isReady被重载
    }

    @Override
    public void onGameTick(int gameTime) {
        if (!gameSpawner.shouldTick()) {
            return;
        }

        gameSpawner.tick(gameTime, GameManager.get().getGameTeams());
    }

    /**
     * 只负责帮 GameManager 传送至大厅，不负责检查
     */
    public void teleportToLobby(@NotNull ServerPlayer player) {
        if (!isLobbyCreated()) {
            return;
        }
        GameManager.get().safeTeleport(player, lobbyPos);
        BattleRoyale.LOGGER.info("Teleport player {} (UUID: {}) to lobby ({}, {}, {})", player.getName(), player.getUUID(), lobbyPos.x, lobbyPos.y, lobbyPos.z);
    }

    /**
     * 类内部负责的传送，类内调用前进行检查
     */
    private void teleportGamePlayerToLobby(@NotNull GamePlayer gamePlayer, @NotNull ServerLevel serverLevel) {
        UUID id = gamePlayer.getPlayerUUID();
        ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(id);
        if (player == null) {
            return;
        }
        teleportToLobby(player);
    }

    public boolean canMuteki(ServerPlayer serverPlayer) {
        UUID id = serverPlayer.getUUID();
        if (TeamManager.get().hasStandingGamePlayer(id)) { // 游戏中的玩家不能无敌
            return false;
        }
        return isInLobby(serverPlayer.position());
    }

    /**
     * 调用时保证 lobbyPos 和 lobbyDimension 非空
     * @param pos 需要判断的位置
     * @return 判定结果
     */
    private boolean isInLobby(Vec3 pos) {
        double minX = lobbyPos.x - lobbyDimension.x;
        double maxX = lobbyPos.x + lobbyDimension.x;
        double minY = lobbyPos.y - lobbyDimension.y;
        double maxY = lobbyPos.y + lobbyDimension.y;
        double minZ = lobbyPos.z - lobbyDimension.z;
        double maxZ = lobbyPos.z + lobbyDimension.z;

        return pos.x >= minX && pos.x <= maxX &&
                pos.y >= minY && pos.y <= maxY &&
                pos.z >= minZ && pos.z <= maxZ;
    }

    public boolean isLobbyCreated() {
        // return prepared || ready || GameManager.get().isInGame(); // 任意阶段均保证大厅已创建
        return lobbyPos != null; // 让游戏结束后也能传送回大厅
    }

    public void sendLobbyInfo(ServerPlayer player) {
        if (player == null) {
            return;
        }

        if (isLobbyCreated()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_pos", lobbyPos.x, lobbyPos.y, lobbyPos.z).withStyle(ChatFormatting.AQUA));
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_dimension", lobbyDimension.x, lobbyDimension.y, lobbyDimension.z).withStyle(ChatFormatting.AQUA));
            if (lobbyMuteki) ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_muteki").withStyle(ChatFormatting.GOLD));
        } else { // 没有创建大厅
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }

    public void sendLobbyInfo(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        if (isLobbyCreated()) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_pos", lobbyPos.x, lobbyPos.y, lobbyPos.z).withStyle(ChatFormatting.AQUA));
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_dimension", lobbyDimension.x, lobbyDimension.y, lobbyDimension.z).withStyle(ChatFormatting.AQUA));
            if (lobbyMuteki) ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_muteki").withStyle(ChatFormatting.GOLD));
        } else { // 没有创建大厅
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }

    /**
     * Compatibility to PUBGMC
     */
    public boolean setPubgmcLobby(Vec3 corrds, double radius) {
        if (corrds == null || radius <= 0) {
            return false;
        }
        this.lobbyPos = corrds;
        this.lobbyDimension = new Vec3(radius, radius, radius);
        return true;
    }
}
