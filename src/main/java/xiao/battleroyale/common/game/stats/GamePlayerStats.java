package xiao.battleroyale.common.game.stats;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.stats.type.DownRecord;
import xiao.battleroyale.common.game.stats.type.HurtRecord;
import xiao.battleroyale.common.game.stats.type.KillRecord;
import xiao.battleroyale.common.game.stats.type.ReviveRecord;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamePlayerStats extends AbstractStats {

    public final UUID playerUUID;
    public final String playerName;
    public final int gameSingleId;
    public final int teamId;
    public final String gameTeamColor;
    public final boolean isBot;

    private boolean isFinished = false;
    private int gameRank = StatsManager.DEFAULT_RANK;
    public int getGameRank() { return gameRank; }

    private final List<HurtRecord> hurtRecords = new ArrayList<>();
    private final List<DownRecord> downRecords = new ArrayList<>();
    private final List<KillRecord> killRecords = new ArrayList<>();
    private final List<ReviveRecord> reviveRecords = new ArrayList<>();

    public GamePlayerStats(GamePlayer gamePlayer) {
        super();
        this.playerUUID = gamePlayer.getPlayerUUID();
        this.playerName = gamePlayer.getPlayerName();
        this.gameSingleId = gamePlayer.getGameSingleId();
        this.teamId = gamePlayer.getGameTeamId();
        this.gameTeamColor = gamePlayer.getGameTeamColor();
        this.isBot = gamePlayer.isBot();
    }

    /**
     * 确定最终个人排名，结束记录
     */
    public void setFinalRank(int gameRank) {
        this.gameRank = gameRank;
        this.isFinished = true;
    }

    public void addHurtRecord(HurtRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add hurt record to finished game player stats");
            return;
        }

        // 连续造成的伤害合并记录
        if (!hurtRecords.isEmpty()) {
            HurtRecord lastRecord = hurtRecords.get(hurtRecords.size()-1);
            if (lastRecord.canStack(newRecord)) {
                lastRecord.stackRecord(newRecord);
                return;
            }
        }
        hurtRecords.add(newRecord);
    }

    public void addDownRecord(DownRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add down record to finished game player stats");
            return;
        }

        downRecords.add(newRecord);
    }

    public void addKillRecord(KillRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add kill record to finished game player stats");
            return;
        }

        killRecords.add(newRecord);
    }

    public void addReviveRecord(ReviveRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add revive record to finished game player stats");
            return;
        }

        reviveRecords.add(newRecord);
    }
}