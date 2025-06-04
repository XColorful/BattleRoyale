package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.event.game.*;
import xiao.battleroyale.util.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class GameManager extends AbstractGameManager {

    private static GameManager instance;

    private int gameTime = 0; // 游戏运行时维护当前游戏时间
    private UUID gameId;
    private boolean inGame;
    private SyncData syncData = new SyncData();
    private ResourceKey<Level> gameDimensionKey;
    private ServerLevel serverLevel;

    // config
    private int gameruleConfigId = 0;
    private int spawnConfigId = 0;
    private int botConfigId = 0;
    private int maxGameTime; // 最大游戏持续时间，配置项
    private boolean recordStats; // 是否在游戏结束后记录日志，配置项
    private int maxInvalidTime = 60; // 最大离线/未加载时间，过期强制淘汰，配置项
    private int getMaxInvalidTick() { return maxInvalidTime * 20; }
    private int maxBotInvalidTime = 10 * 20;
    private boolean removeInvalidTeam = false; // TODO 增加配置，使默认false


    private GameManager() {
        generateGameId();
    }

    // GameManager初始化，并非游戏初始化
    public static void init() {
        if (instance == null) {
            instance = new GameManager();
        }
    }

    @NotNull
    public static GameManager get() {
        if (instance == null) {
            GameManager.init();
        }
        return instance;
    }

    @NotNull
    public UUID getGameId() {
        if (this.gameId == null) {
            generateGameId();
        }
        return this.gameId;
    }

    private void generateGameId() {
        setGameId(UUID.randomUUID());
    }

    public void setGameId(UUID gameId) {
        if (this.inGame) {
            return;
        }
        this.gameId = gameId;
    }

    public boolean isInGame() {
        return inGame;
    }

    /**
     * 检测并加载游戏配置，不应该执行任何实际内容
     */
    public void initGameConfig(ServerLevel serverLevel) {
        if (isInGame()) {
            return;
        }
        this.serverLevel = serverLevel;

        BattleroyaleEntry brEntry = GameConfigManager.get().getGameruleConfig(gameruleConfigId).getBattleRoyaleEntry();
        maxGameTime = brEntry.maxGameTime;
        recordStats = brEntry.recordGameStats;

        GameLootManager.get().initGameConfig(serverLevel);
        GameruleManager.get().initGameConfig(serverLevel);
        SpawnManager.get().initGameConfig(serverLevel);
        TeamManager.get().initGameConfig(serverLevel);
        ZoneManager.get().initGameConfig(serverLevel);

        if (GameLootManager.get().isPreparedForGame() // 判定的优先级最低
                && GameruleManager.get().isPreparedForGame()
                && SpawnManager.get().isPreparedForGame()
                && TeamManager.get().isPreparedForGame()
                && ZoneManager.get().isPreparedForGame()) {
            this.prepared = true;
            // 注册事件
            LogEventHandler.get().register(); // 后续玩家登录可根据配置直接加入队伍
        } else {
            this.prepared = false;
        }
    }

    public int getPlayerLimit() { return TeamManager.get().getPlayerLimit(); }
    public List<GameTeam> getGameTeams() { return TeamManager.get().getGameTeamsList(); }
    public @Nullable GameTeam getGameTeamById(int teamId) { return TeamManager.get().getGameTeamById(teamId); }
    public List<GamePlayer> getGamePlayers() { return TeamManager.get().getGamePlayersList(); }
    public List<GamePlayer> getStandingGamePlayers() { return TeamManager.get().getStandingGamePlayersList(); }

    /**
     * 准备游戏，将玩家传送至大厅等
     * @param serverLevel 当前 level
     */
    @Override
    public void initGame(ServerLevel serverLevel) {
        if (isInGame()) {
            return;
        }

        if (!prepared || this.serverLevel != serverLevel) {
            initGameConfig(serverLevel);
            if (!prepared) {
                return;
            }
        }
        // 同步信息
        this.syncData.initGame();
        SyncEventHandler.get().register();

        GameLootManager.get().initGame(serverLevel);
        TeamManager.get().initGame(serverLevel);
        GameruleManager.get().initGame(serverLevel); // Gamerule会进行一次默认游戏模式切换
        SpawnManager.get().initGame(serverLevel); // SpawnManager会进行一次传送，放在TeamManager之后
        ZoneManager.get().initGame(serverLevel);

        if (isReadyForGame()) {
            generateGameId(); // 手动刷新 gameId
            ready = true;
        }
    }

    private boolean isReadyForGame() {
        return GameLootManager.get().isReady()
                && GameruleManager.get().isReady()
                && SpawnManager.get().isReady()
                && TeamManager.get().isReady()
                && ZoneManager.get().isReady();
    }

    /**
     * 开始游戏，需要在开始瞬间进行额外判定
     */
    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (isInGame()) {
            return false;
        }
        if (!ready || this.serverLevel != serverLevel) {
            initGame(serverLevel);
            if (!ready) {
                return false;
            }
        }
        checkAndUpdateInvalidPlayer();
        if (GameLootManager.get().startGame(serverLevel) // 判定的优先级最低
                && TeamManager.get().startGame(serverLevel) // 先执行 TeamManager 得到 StandingGamePlayers，并确保无队伍玩家均被清理
                && GameruleManager.get().startGame(serverLevel)
                && ZoneManager.get().startGame(serverLevel)
                && SpawnManager.get().startGame(serverLevel)) { // 最后执行 SpawnManager 先tick一次（传送玩家）
            this.gameDimensionKey = serverLevel.dimension();
            this.inGame = true;
            this.ready = false;
            this.gameTime = 0; // 游戏结束后不手动重置
            // 注册事件监听
            DamageEventHandler.get().register();
            LoopEventHandler.get().register();
            PlayerEventHandler.get().register();
            // 重置同步信息
            this.syncData.startGame();
            return true;
        } else {
            stopGame(this.serverLevel);
            return false;
        }
    }

    /**
     * 外部调用接口
     */
    public void onGameTick() {
        if (this.serverLevel == null) { // 当前level未加载或者超过最大时长
            BattleRoyale.LOGGER.warn("GameManager cached serverLevel is null, stopped game");
            stopGame(null);
        }

        this.gameTime++;
        if (this.gameTime > this.maxGameTime) { // 超过最大游戏时长
            stopGame(this.serverLevel);
            ChatUtils.sendTranslatableMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.reach_max_game_time").withStyle(ChatFormatting.GRAY));
            BattleRoyale.LOGGER.info("Reached max game time ({}) and force stopped", this.maxGameTime);
        }
        onGameTick(this.gameTime);
    }

    /**
     * 游戏主逻辑，调度各 Manager，向客户端通信
     */
    public void onGameTick(int gameTime) {
        checkAndUpdateInvalidPlayer();

        GameLootManager.get().onGameTick(gameTime);

        TeamManager.get().onGameTick(gameTime);
        GameruleManager.get().onGameTick(gameTime);
        SpawnManager.get().onGameTick(gameTime);
        ZoneManager.get().onGameTick(gameTime);
    }

    public void syncInfo() {
        this.syncData.syncInfo(gameTime);
    }


    /**
     * 检查所有未淘汰玩家是否在线，更新不在线时长或更新最后有效位置
     * 检查队伍成员是否均为倒地或者不在线，淘汰队伍（所有成员）
     */
    public void checkAndUpdateInvalidPlayer() {
        List<GamePlayer> invalidPlayers = new ArrayList<>();
        // 筛选并增加无效时间计数
        for (GamePlayer gamePlayer : getStandingGamePlayers()) {
            ServerPlayer serverPlayer = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
            if (serverPlayer == null) { // 不在线或者不在游戏运行的 level
                gamePlayer.setActiveEntity(false);
                if (eliminateInactiveTeam(gamePlayer)) {
                    continue;
                }
                gamePlayer.addInvalidTime();
                if (!gamePlayer.isBot()) { // 玩家离线时间检查
                    if (gamePlayer.getInvalidTime() >= getMaxInvalidTick()) { // 达到允许的最大离线时间
                        invalidPlayers.add(gamePlayer);
                    }
                } else { // 人机离线时间检查
                    if (gamePlayer.getInvalidTime() >= maxBotInvalidTime) {
                        invalidPlayers.add(gamePlayer);
                    }
                }
            } else { // 更新最后有效位置
                gamePlayer.setActiveEntity(true);
                gamePlayer.setLastPos(serverPlayer.position());
            }
        }
        // 清理无效玩家
        if (!invalidPlayers.isEmpty()) {
            for (GamePlayer invalidPlayer : invalidPlayers) {
                if (TeamManager.get().forceEliminatePlayerSilence(invalidPlayer)) { // 强制淘汰了玩家，不一定都在此处淘汰
                    ChatUtils.sendTranslatableMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", invalidPlayer.getPlayerName()).withStyle(ChatFormatting.GRAY));
                    BattleRoyale.LOGGER.info("Force eliminated GamePlayer {} (UUID: {})", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
                }
            }
        }
    }

    /**
     * 检查是否只有倒地或不在线玩家，逐个淘汰
     * 默认不开启，以防玩家倒地的时候队友离线导致 kibo 破灭
     */
    private boolean eliminateInactiveTeam(GamePlayer invalidPlayer) {
        if (!removeInvalidTeam) {
            return false;
        }
        GameTeam gameTeam = invalidPlayer.getTeam();
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (teamMember.isActiveEntity() || teamMember.isAlive()) { // 有在线的未倒地玩家
                return false;
            }
        }
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (TeamManager.get().forceEliminatePlayerSilence(teamMember)) {
                ChatUtils.sendTranslatableMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", teamMember.getPlayerName()).withStyle(ChatFormatting.GRAY));
                BattleRoyale.LOGGER.info("Force eliminated GamePlayer {} (UUID: {}) for inactive team", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
            }
        }
        return true;
    }

    @Nullable
    public ServerLevel getServerLevel() {
        return this.serverLevel;
    }

    /**
     * 完整检查所有队伍情况，淘汰无在线玩家的队伍
     */
    public void checkIfGameShouldEnd() {
        if (!this.inGame) {
            return;
        }

        checkAndUpdateInvalidPlayer();
        if (TeamManager.get().getStandingTeamCount() <= 1) {
            stopGame(this.serverLevel);
        }
        // TODO 剩余检查
    }

    /**
     * 强制终止游戏
     */
    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        GameLootManager.get().stopGame(serverLevel);
        ZoneManager.get().stopGame(serverLevel);
        SpawnManager.get().stopGame(serverLevel);
        GameruleManager.get().stopGame(serverLevel);
        TeamManager.get().stopGame(serverLevel); // TeamManager最后处理
        this.prepared = false;
        this.inGame = false;
        this.ready = false;
        // 取消事件监听
        DamageEventHandler.get().unregister();
        LoopEventHandler.get().unregister();
        PlayerEventHandler.get().unregister();
        LogEventHandler.get().unregister();
        SyncEventHandler.get().unregister();
        // 清空同步信息
        this.syncData.endGame();
        this.syncData.clear();
    }

    public boolean teleportToLobby(@NotNull ServerPlayer player) {
        if (SpawnManager.get().isLobbyCreated()) {
            SpawnManager.get().teleportToLobby(player);
            return true;
        } else {
            return false;
        }
    }

    public void onPlayerLoggedIn(ServerPlayer player) {
        GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            if (GameManager.get().isInGame() && gamePlayer.isEliminated()) {
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
            }
            return;
        }

        if (TeamManager.get().shouldAutoJoin() && !this.inGame) { // 没开游戏就加入
            TeamManager.get().joinTeam(player);
            teleportToLobby(player); // 自动传到大厅
        }
    }

    public void onPlayerLoggedOut(ServerPlayer player) {
        if (!this.inGame) {
            TeamManager.get().removePlayerFromTeam(player.getUUID()); // 没开始游戏就直接踢了
        }

        GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            gamePlayer.setActiveEntity(false);
            checkIfGameShouldEnd();
        }
    }

    public void onPlayerDeath(ServerPlayer player) {
        GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            return;
        }

        gamePlayer.setAlive(false); // GamePlayer内部会自动更新eliminated
        if (!player.isAlive()) {
            gamePlayer.setEliminated(true);
        }
        if (gamePlayer.isEliminated()) {
            TeamManager.get().forceEliminatePlayerSilence(gamePlayer); // 提醒 TeamManager 内部更新 standingPlayer信息
        }

        GameTeam gameTeam = gamePlayer.getTeam();
        if (gameTeam.isTeamEliminated()) {
            BattleRoyale.LOGGER.info("Team {} has been eliminated", gameTeam.getGameTeamId());
            if (this.serverLevel != null) {
                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
            }
            checkIfGameShouldEnd();
        }
    }

    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     */
    public void safeTeleport(@NotNull ServerPlayer player, Vec3 teleportPos) {
        safeTeleport(player, teleportPos.x, teleportPos.y, teleportPos.z);
    }

    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     */
    public void safeTeleport(@NotNull ServerPlayer player, double x, double y, double z) {
        player.fallDistance = 0;
        player.teleportTo(x, y, z);
    }

    public int getGameTime() { return this.gameTime; }

    public Supplier<Float> getRandom() {
        return () -> this.serverLevel.getRandom().nextFloat();
    }

    public void addZoneInfo(int id, @Nullable CompoundTag zoneInfo) { this.syncData.addZoneInfo(id, zoneInfo); }
    public void addChangedTeamInfo(int teamId) {
        GameTeam gameTeam = TeamManager.get().getGameTeamById(teamId);
        if (gameTeam != null) {
            for (GamePlayer gamePlayer : gameTeam.getTeamMembers()) {
                this.syncData.deleteLeavedMember(gamePlayer.getPlayerUUID());
            }
        }
        this.syncData.addChangedTeam(teamId);
    }
    public void addLeavedMember(UUID playerUUID) { this.syncData.addLeavedMember(playerUUID); }

    public int getGameruleConfigId() { return gameruleConfigId; }
    public int getSpawnConfigId() { return spawnConfigId; }
    public int getBotConfigId() { return botConfigId; }

    // 用指令设置默认配置
    public boolean setGameruleConfigId(int gameId) {
        if (gameId < 0 || GameConfigManager.get().getGameruleConfig(gameId) == null) {
            return false;
        }
        this.gameruleConfigId = gameId;
        return true;
    }
    public boolean setSpawnConfigId(int id) {
        if (id < 0 || GameConfigManager.get().getSpawnConfig(id) == null) {
            return false;
        }
        this.spawnConfigId = id;
        return true;
    }
    public boolean setBotConfigId(int id) {
        if (id < 0 || BotConfigManager.get().getBotConfig(id) == null) {
            return false;
        }
        this.botConfigId = id;
        return true;
    }
}
