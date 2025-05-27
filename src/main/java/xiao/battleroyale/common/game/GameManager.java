package xiao.battleroyale.common.game;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;

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

    private int gameTime;
    private int maxGameTime;
    private boolean recordStats;

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
        this.gameId = gameId;
    }

    public boolean isInGame() {
        return inGame;
    }

    /**
     * 检测并初始化游戏配置
     */
    public void initGameConfig(ServerLevel serverLevel) {
        if (isInGame()) { // 禁止游戏运行中的意外修改
            return;
        }
        BattleroyaleEntry brEntry = GameruleConfigManager.get().getGameruleConfig(gameruleConfigId).getBattleRoyaleEntry();
        maxGameTime = brEntry.maxGameTime;
        recordStats = brEntry.recordGameStats;

        GameruleManager.get().initGameConfig(serverLevel);
        SpawnManager.get().initGameConfig(serverLevel);
        TeamManager.get().initGameConfig(serverLevel);
        ZoneManager.get().initGameConfig(serverLevel);

        prepared = GameruleManager.get().isPreparedForGame()
                && SpawnManager.get().isPreparedForGame()
                && TeamManager.get().isPreparedForGame()
                && ZoneManager.get().isPreparedForGame();
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
        if (!prepared) {
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
     * 是否可以立即开始游戏
     * @return 判定结果
     */
    @Override
    public boolean isReady() {
        return ready;
    }

    /**
     * 开始游戏
     */
    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (isInGame()) {
            return false;
        }
        if (!ready) {
            initGame(serverLevel);
            if (!ready) {
                return false;
            }
        }
        if ( TeamManager.get().startGame(serverLevel)
                && GameruleManager.get().startGame(serverLevel)
                && SpawnManager.get().startGame(serverLevel)
                && ZoneManager.get().startGame(serverLevel)) {
            this.gameDimensionKey = serverLevel.dimension();
            this.serverLevel = serverLevel;
            this.inGame = true;
            this.ready = false;
            return true;
        }
        return false;
    }

    @Nullable
    public ServerLevel getServerLevel() {
        if (!this.inGame) {
            return null;
        }
        return this.serverLevel;
    }

    public void checkIfGameShouldEnd() {
        ;
    }

    /**
     * 强制终止游戏
     */
    @Override
    public void stopGame(ServerLevel serverLevel) {
        ZoneManager.get().stopGame(serverLevel);
        SpawnManager.get().stopGame(serverLevel);
        GameruleManager.get().stopGame(serverLevel);
        TeamManager.get().stopGame(serverLevel); // TeamManager最后处理
        this.prepared = false;
        this.inGame = false;
        this.ready = false;
    }

    public int getGameruleConfigId() { return gameruleConfigId; }
    public int getSpawnConfigId() { return spawnConfigId; }
    public int getBotConfigId() { return botConfigId; }

    // set config id via command
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
