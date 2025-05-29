package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.event.game.DamageEventHandler;
import xiao.battleroyale.event.game.LogEventHandler;
import xiao.battleroyale.event.game.LoopEventHandler;
import xiao.battleroyale.event.game.PlayerEventHandler;
import xiao.battleroyale.util.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameManager extends AbstractGameManager implements IGameManager {

    private static GameManager instance;

    private UUID gameId;
    private boolean inGame;
    private int gameruleConfigId = 1;
    private int spawnConfigId = 1;
    private int botConfigId = 1;
    private ResourceKey<Level> gameDimensionKey;
    private ServerLevel serverLevel;

    private int gameTime; // 游戏运行时维护当前游戏时间
    private int maxGameTime; // 最大游戏持续时间，配置项
    private boolean recordStats; // 是否在游戏结束后记录日志，配置项
    private int maxInvalidTime = 60; // 最大离线/未加载时间，过期强制淘汰，配置项
    private int getMaxInvalidTick() { return maxInvalidTime * 20; }
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

        BattleroyaleEntry brEntry = GameruleConfigManager.get().getGameruleConfig(gameruleConfigId).getBattleRoyaleEntry();
        maxGameTime = brEntry.maxGameTime;
        recordStats = brEntry.recordGameStats;

        GameruleManager.get().initGameConfig(serverLevel);
        SpawnManager.get().initGameConfig(serverLevel);
        TeamManager.get().initGameConfig(serverLevel);
        ZoneManager.get().initGameConfig(serverLevel);

        if (GameruleManager.get().isPreparedForGame()
                && SpawnManager.get().isPreparedForGame()
                && TeamManager.get().isPreparedForGame()
                && ZoneManager.get().isPreparedForGame()) {
            this.prepared = true;
            // 注册登录事件
            LogEventHandler.getInstance().register(); // 后续玩家登录可根据配置直接加入队伍
        } else {
            this.prepared = false;
        }
    }

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
        TeamManager.get().initGame(serverLevel);
        GameruleManager.get().initGame(serverLevel); // Gamerule会进行一次默认游戏模式切换
        SpawnManager.get().initGame(serverLevel); // SpawnManager会进行一次传送，放在TeamManager之后
        ZoneManager.get().initGame(serverLevel);

        if (GameruleManager.get().isReady() && SpawnManager.get().isReady() && TeamManager.get().isReady() && ZoneManager.get().isReady()) {
            ready = true;
        }
    }

    /**
     * 是否之前准备好开始游戏，不代表后续能秒开
     * @return 判定结果
     */
    @Override
    public boolean isReady() {
        return ready;
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
        if (TeamManager.get().startGame(serverLevel) // 先执行 TeamManager 得到 StandingGamePlayers，并确保无队伍玩家均被清理
                && GameruleManager.get().startGame(serverLevel)
                && ZoneManager.get().startGame(serverLevel)
                && SpawnManager.get().startGame(serverLevel)) { // 最后执行 SpawnManager 传送玩家
            this.gameDimensionKey = serverLevel.dimension();
            this.inGame = true;
            this.ready = false;
            this.gameTime = 0; // 游戏结束后不手动重置
            // 注册事件监听
            DamageEventHandler.getInstance().register();
            LoopEventHandler.getInstance().register();
            PlayerEventHandler.getInstance().register();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 游戏主逻辑，调度各 Manager
     */
    public void onGameTick() {
        if (this.serverLevel == null) {
            stopGame(null);
        }

        checkAndUpdateInvalidPlayer();
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
                if (gamePlayer.getInvalidTime() >= getMaxInvalidTick()) { // 达到允许的最大离线时间
                    invalidPlayers.add(gamePlayer);
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
                    ChatUtils.sendTranslatableMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player").withStyle(ChatFormatting.GRAY));
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
            if (teamMember.isActiveEntity() || teamMember.isAlive()) { // 有一个在线的未倒地的玩家
                return false;
            }
        }
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (TeamManager.get().forceEliminatePlayerSilence(teamMember)) {
                ChatUtils.sendTranslatableMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player").withStyle(ChatFormatting.GRAY));
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
        // TODO 剩余检查
    }

    /**
     * 强制终止游戏
     */
    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        ZoneManager.get().stopGame(serverLevel);
        SpawnManager.get().stopGame(serverLevel);
        GameruleManager.get().stopGame(serverLevel);
        TeamManager.get().stopGame(serverLevel); // TeamManager最后处理
        this.prepared = false;
        this.inGame = false;
        this.ready = false;
        // 取消事件监听
        DamageEventHandler.getInstance().unregister();
        LoopEventHandler.getInstance().unregister();
        PlayerEventHandler.getInstance().unregister();
        LogEventHandler.getInstance().unregister();
    }

    public int getGameruleConfigId() { return gameruleConfigId; }
    public int getSpawnConfigId() { return spawnConfigId; }
    public int getBotConfigId() { return botConfigId; }

    // 用指令设置默认配置
    public boolean setGameruleConfigId(int gameId) {
        if (gameId < 0 || GameruleConfigManager.get().getGameruleConfig(gameId) == null) {
            return false;
        }
        this.gameruleConfigId = gameId;
        return true;
    }
    public boolean setSpawnConfigId(int id) {
        if (id < 0 || SpawnConfigManager.get().getSpawnConfig(id) == null) {
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
