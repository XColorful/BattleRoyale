package xiao.battleroyale.common.game.spawn;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportData;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportFinishData;
import xiao.battleroyale.api.game.spawn.IGameLobbyReadApi;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.GameUtilsFunction;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.event.game.LobbyEventHandler;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.util.List;
import java.util.UUID;

/**
 * 管理玩家出生方式、传送相关的Manager
 */
public class SpawnManager extends AbstractGameManager implements IGameLobbyReadApi {

    private static class SpawnManagerHolder {
        private static final SpawnManager INSTANCE = new SpawnManager();
    }

    public static SpawnManager get() {
        return SpawnManagerHolder.INSTANCE;
    }

    private SpawnManager() {}

    public static void init(McSide mcSide) {
        ;
    }

    private boolean initGameTeleport = true;
    private Vec3 lobbyPos;
    private Vec3 lobbyDimension;
    private boolean lobbyMuteki = true;
    private boolean lobbyHeal = true;
    private boolean changeGamemode = true;
    private boolean teleportDropInventory = false;
    private boolean teleportClearInventory = false;
    private IGameSpawner gameSpawner;

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        int spawnConfigId = GameManager.get().getSpawnConfigId();
        SpawnConfig spawnConfig = (SpawnConfig) GameConfigManager.get().getConfigEntry(SpawnConfigManager.get().getNameKey(), spawnConfigId);
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
        GameruleConfig gameruleConfig = (GameruleConfig) GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), gameId);
        if (gameruleConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }

        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        if (brEntry == null) {
            BattleRoyale.LOGGER.debug("Gamerule config missing brEntry");
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        setLobby(brEntry.lobbyCenterPos, brEntry.lobbyDimension, brEntry.lobbyMuteki, brEntry.lobbyHeal, brEntry.lobbyChangeGamemode, brEntry.lobbyTeleportDropInventory, brEntry.lobbyTeleportClearInventory);
        if (!isLobbyCreated()) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }

        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (gameEntry == null) {
            BattleRoyale.LOGGER.debug("Gamerule config missing gameEntry");
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        this.initGameTeleport = gameEntry.teleportWhenInitGame;


        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("SpawnManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }
        if (!this.configPrepared) {
            return;
        }

        // 传送至大厅
        if (this.initGameTeleport) {
            List<GamePlayer> gamePlayerList = GameTeamManager.getGamePlayers();
            for (GamePlayer gamePlayer : gamePlayerList) {
                teleportGamePlayerToLobby(gamePlayer, serverLevel);
            }
            BattleRoyale.LOGGER.debug("SpawnManager::initGame teleported all game player to lobby");
        }

        this.gameSpawner.clear();
        this.gameSpawner.init(GameManager.get().getRandom(), GameTeamManager.getPlayerLimit()); // 用玩家上限作为点位数量
        if (!isReady()) {
            return;
        }
        this.configPrepared = false;
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
        this.configPrepared = false;
        // this.ready = false; // isReady被重载
    }

    @Override
    public void onGameTick(int gameTime) {
        if (!gameSpawner.shouldTick()) {
            return;
        }

        gameSpawner.tick(gameTime, GameTeamManager.getGameTeams());
    }

    public void healPlayer(@NotNull ServerPlayer player) {
        if (PlayerRevive.get().isBleeding(player)) {
            PlayerRevive.get().revive(player);
        }
        player.removeAllEffects();
        player.heal(player.getMaxHealth()); // heal会触发事件
        player.getFoodData().setFoodLevel(20);
    }

    /**
     * 只负责帮 GameManager 传送至大厅，不负责检查
     */
    public void teleportToLobby(@NotNull ServerPlayer player) {
        GameManager gameManager = GameManager.get();
        if (EventPoster.postEvent(new GameLobbyTeleportData(gameManager, player))) {
            BattleRoyale.LOGGER.debug("LobbyTeleportEvent canceled, skipped teleportToLobby (ServerPlayer {})", player.getName().getString());
            return;
        }

        if (!isLobbyCreated()) {
            BattleRoyale.LOGGER.debug("Lobby is not created, failed to teleport player {} (UUID:{}) to lobby", player.getName().getString(), player.getUUID());
            return;
        }
        if (lobbyHeal) {
            healPlayer(player);
        }
        if (changeGamemode) {
            player.setGameMode(GameruleManager.get().getGameMode());
        }
        if (teleportDropInventory) {
            player.getInventory().dropAll();
        }
        if (teleportClearInventory) {
            player.getInventory().clearContent();
        }
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel != null) {
            GameUtilsFunction.safeTeleport(player, serverLevel, lobbyPos, 0, 0);
        } else {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, teleport to literal position");
            GameUtilsFunction.safeTeleport(player, lobbyPos);
        }
        BattleRoyale.LOGGER.info("Teleport player {} (UUID: {}) to lobby ({}, {}, {})", player.getName().getString(), player.getUUID(), lobbyPos.x, lobbyPos.y, lobbyPos.z);
        EventPoster.postEvent(new GameLobbyTeleportFinishData(gameManager, player));
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

    public boolean setLobby(Vec3 centerPos, Vec3 dimension, boolean shouldMuteki, boolean shouldHeal, boolean changeGamemode, boolean teleportDropInventory, boolean teleportClearInventory) {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is in game, SpawnManager skipped set lobby");
            return false;
        }
        if (Vec3Utils.hasNegative(dimension)) {
            BattleRoyale.LOGGER.warn("SpawnManager: dimension:{} has negative, reject to apply", dimension);
            return false;
        }
        this.lobbyPos = centerPos;
        this.lobbyMuteki = shouldMuteki;
        // 大厅无敌（监听伤害事件）
        if (this.lobbyMuteki) {
            LobbyEventHandler.register();
        } else {
            LobbyEventHandler.unregister();
        }
        this.lobbyHeal = shouldHeal;
        this.lobbyDimension = dimension;
        this.changeGamemode = changeGamemode;
        this.teleportDropInventory = teleportDropInventory;
        this.teleportClearInventory = teleportClearInventory;
        BattleRoyale.LOGGER.debug("Successfully set lobby: center{}, dim{}", lobbyPos, lobbyDimension);
        return true;
    }

    /**
     * Compatibility to PUBGMC
     */
    public boolean setPubgmcLobby(Vec3 centerPos, double radius) {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is in game, SpawnManager skipped set lobby");
            return false;
        }
        if (radius < 0) {
            BattleRoyale.LOGGER.warn("SpawnManager: radius:{} has negative, reject to apply", radius);
            return false;
        }
        setLobby(centerPos, new Vec3(radius, radius, radius), this.lobbyMuteki, this.lobbyHeal, this.changeGamemode, this.teleportDropInventory, this.teleportClearInventory);
        return true;
    }

    // --------GameApi--------

    @Override public boolean isLobbyCreated() {
        // return configPrepared || ready || GameManager.get().isInGame(); // 任意阶段均保证大厅已创建
        return lobbyPos != null && lobbyDimension != null; // 让游戏结束后也能传送回大厅
    }
    @Override public ResourceKey<Level> lobbyLevelKey() {
        return GameManager.get().getGameLevelKey();
    }
    @Override public Vec3 lobbyPos() {
        return this.lobbyPos;
    }
    @Override public Vec3 lobbyDimension() {
        return this.lobbyDimension;
    }
    @Override public boolean lobbyMuteki() {
        return this.lobbyMuteki;
    }
    @Override public boolean lobbyHeal() {
        return this.lobbyHeal;
    }
    @Override public boolean lobbyChangeGamemode() {
        return this.changeGamemode;
    }
    @Override public boolean teleportDropInventory() {
        return this.teleportDropInventory;
    }
    @Override public boolean teleportClearInventory() {
        return this.teleportClearInventory;
    }

    /**
     * 调用时保证 lobbyPos 和 lobbyDimension 非空
     * @param pos 需要判断的位置
     * @return 判定结果
     */
    @Override public boolean isInLobbyRange(Vec3 pos) {
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
    @Override public boolean canMuteki(@NotNull LivingEntity livingEntity) {
        if (!isLobbyCreated() || GameTeamManager.hasStandingGamePlayer(livingEntity.getUUID())) { // 游戏中的玩家不能无敌
            return false;
        }

        return livingEntity.level().dimension().equals(this.lobbyLevelKey())
                && isInLobbyRange(livingEntity.position());
    }

    @Override public void sendLobbyInfo(ServerPlayer player) {
        if (player == null) {
            return;
        }

        if (isLobbyCreated()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_pos", lobbyPos.x, lobbyPos.y, lobbyPos.z).withStyle(ChatFormatting.AQUA));
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_dimension", lobbyDimension.x, lobbyDimension.y, lobbyDimension.z).withStyle(ChatFormatting.AQUA));
            if (lobbyMuteki) ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_muteki").withStyle(ChatFormatting.GOLD));
            if (lobbyHeal) ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_heal").withStyle(ChatFormatting.GREEN));
        } else { // 没有创建大厅
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }
    @Override public void sendLobbyInfo(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        if (isLobbyCreated()) {
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_pos", lobbyPos.x, lobbyPos.y, lobbyPos.z).withStyle(ChatFormatting.AQUA));
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_dimension", lobbyDimension.x, lobbyDimension.y, lobbyDimension.z).withStyle(ChatFormatting.AQUA));
            if (lobbyMuteki) ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_muteki").withStyle(ChatFormatting.GOLD));
            if (lobbyHeal) ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_heal").withStyle(ChatFormatting.GREEN));
        } else { // 没有创建大厅
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }
}
