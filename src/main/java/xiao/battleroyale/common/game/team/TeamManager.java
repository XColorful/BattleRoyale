package xiao.battleroyale.common.game.team;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamManager extends AbstractGameManager {

    private static TeamManager instance;

    private int playerTotal;
    private int teamSize;
    private boolean aiTeammate;
    private boolean aiEnemy;
    private boolean autoJoinGame;

    private final List<UUID> playerIdList = new ArrayList<>();

    private TeamManager() {
        ;
    }

    public static void init() {
        if (instance == null) {
            instance = new TeamManager();
        }
    }

    @NotNull
    public static TeamManager get() {
        if (instance == null) {
            TeamManager.init();
        }
        return instance;
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        int gameId = GameManager.get().getGameruleConfigId();
        BattleroyaleEntry brEntry = GameruleConfigManager.get().getGameruleConfig(gameId).getBattleRoyaleEntry();
        if (brEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get BattleroyaleEntry from GameruleConfig by id: {}", gameId);
            return;
        }
        this.playerTotal = brEntry.playerTotal;
        this.teamSize = brEntry.teamSize;
        this.aiTeammate = brEntry.aiTeammate;
        this.aiEnemy = brEntry.aiEnemy;
        this.autoJoinGame = brEntry.autoJoinGame;

        if (playerTotal < 1 || teamSize < 1) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Invalid BattleroyaleEntry for TeamManager in initGameConfig");
            return;
        }

        this.prepared = true;
    }

    /**
     * 如果 autoJoinGame 则自动所有玩家加入游戏
     * 先对加入游戏的玩家随机分组
     * @param serverLevel 当前 level
     */
    @Override
    public void initGame(ServerLevel serverLevel) {

        this.ready = true;
    }

    public List<UUID> getPlayerIdList() {
        return playerIdList;
    }

    public void stopGame(ServerLevel serverLevel) {
        this.prepared = false;
    }
}
