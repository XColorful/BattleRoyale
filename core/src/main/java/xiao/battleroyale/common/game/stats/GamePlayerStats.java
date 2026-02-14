package xiao.battleroyale.common.game.stats;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.stats.event.*;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class GamePlayerStats extends AbstractStats {

    public final @NotNull GamePlayer gamePlayer;

    private boolean isFinished = false;
    private int gameRank = StatsManager.DEFAULT_RANK;

    public int getGameRank() {
        return gameRank;
    }

    private final List<HurtRecord> hurtRecords = new ArrayList<>();
    private final List<DamageRecord> damageRecords = new ArrayList<>();
    private final List<KnockRecord> knockRecords = new ArrayList<>();
    private final List<DownRecord> downRecords = new ArrayList<>();
    private final List<ReviveRecord> reviveRecords = new ArrayList<>();
    private final List<KillRecord> killRecords = new ArrayList<>();
    private final List<DeathRecord> deathRecords = new ArrayList<>();

    public GamePlayerStats(@NotNull GamePlayer gamePlayer) {
        super();
        this.gamePlayer = gamePlayer;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    /**
     * 确定最终个人排名，结束记录
     */
    public void setFinalRank(int gameRank) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to set final rank to finished game player stats");
            return;
        }

        this.gameRank = gameRank;
        this.isFinished = true;
    }

    public int getHurtCount() {
        return hurtRecords.size();
    }
    public int getDamageCount() {
        return damageRecords.size();
    }
    public int getKnockCount() {
        return knockRecords.size();
    }
    public int getDownCount() {
        return downRecords.size();
    }
    public int getReviveCount() {
        return reviveRecords.size();
    }
    public int getKillCount() {
        return killRecords.size();
    }
    public int getDeathCount() {
        return deathRecords.size();
    }

    // 伤害
    public void addHurtRecord(HurtRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add hurt record to finished game player stats");
            return;
        }

        if (!hurtRecords.isEmpty()) {
            HurtRecord lastRecord = hurtRecords.get(hurtRecords.size() - 1);
            if (lastRecord.stackRecord(newRecord)) return;
        }
        hurtRecords.add(newRecord);
    }
    // 被造成伤害
    public void addDamageRecord(DamageRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add damage record to finished game player stats");
            return;
        }

        if (!damageRecords.isEmpty()) {
            DamageRecord lastRecord = damageRecords.get(damageRecords.size() - 1);
            if (lastRecord.stackRecord(newRecord)) return;
        }
        damageRecords.add(newRecord);
    }
    // 击倒
    public void addKnockRecord(KnockRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add knock record to finished game player stats");
            return;
        }

        if (!knockRecords.isEmpty()) {
            KnockRecord lastRecord = knockRecords.get(knockRecords.size() - 1);
            if (lastRecord.stackRecord(newRecord)) return;
        }
        knockRecords.add(newRecord);
    }
    // 倒地
    public void addDownRecord(DownRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add down record to finished game player stats");
            return;
        }

        if (!downRecords.isEmpty()) {
            DownRecord lastRecord = downRecords.get(downRecords.size() - 1);
            if (lastRecord.stackRecord(newRecord)) return;
        }
        downRecords.add(newRecord);
    }
    // 复活
    public void addReviveRecord(ReviveRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add revive record to finished game player stats");
            return;
        }

        if (!reviveRecords.isEmpty()) {
            ReviveRecord lastRecord = reviveRecords.get(reviveRecords.size() - 1);
            if (lastRecord.stackRecord(newRecord)) return;
        }
        reviveRecords.add(newRecord);
    }
    // 淘汰
    public void addKillRecord(KillRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add kill record to finished game player stats");
            return;
        }

        if (!killRecords.isEmpty()) {
            KillRecord lastRecord = killRecords.get(killRecords.size() - 1);
            if (lastRecord.stackRecord(newRecord)) return;
        }
        killRecords.add(newRecord);
    }
    // 被淘汰
    public void addDeathRecord(DeathRecord newRecord) {
        if (isFinished) {
            BattleRoyale.LOGGER.warn("Reject to add death record to finished game player stats");
            return;
        }

        if (!deathRecords.isEmpty()) {
            DeathRecord lastRecord = deathRecords.get(deathRecords.size() - 1);
            if (lastRecord.stackRecord(newRecord)) return;
        }
        deathRecords.add(newRecord);
    }
}