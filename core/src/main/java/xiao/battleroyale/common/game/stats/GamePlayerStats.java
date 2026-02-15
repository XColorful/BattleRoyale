package xiao.battleroyale.common.game.stats;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.stats.IGamePlayerStats;
import xiao.battleroyale.common.game.stats.event.*;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class GamePlayerStats implements IGamePlayerStats {

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
        this.gamePlayer = gamePlayer;
    }

    @Override public @NotNull GamePlayer getGamePlayer() {
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

    @Override public int getHurtCount() {
        return hurtRecords.size();
    }
    @Override public int getDamageCount() {
        return damageRecords.size();
    }
    @Override public int getKnockCount() {
//        return knockRecords.stream()
//                .mapToInt(KnockRecord::getKnockCount)
//                .sum();
        return knockRecords.size();
    }
    @Override public int getDownCount() {
//        return downRecords.stream()
//                .mapToInt(DownRecord::getDownCount)
//                .sum();
        return downRecords.size();
    }
    @Override public int getReviveCount() {
//        return reviveRecords.stream()
//                .mapToInt(ReviveRecord::getReviveCount)
//                .sum();
        return reviveRecords.size();
    }
    @Override public int getKillCount() {
//        return killRecords.stream()
//                .mapToInt(KillRecord::getKillCount)
//                .sum();
        return killRecords.size();
    }
    @Override public int getDeathCount() {
//        return deathRecords.stream()
//                .mapToInt(DeathRecord::getDeathCount)
//                .sum();
        return deathRecords.size();
    }

    @Override public double getHurtAmountTotal() {
        return hurtRecords.stream()
                .mapToDouble(HurtRecord::getHurtAmount)
                .sum();
    }
    @Override public double getDamageAmountTotal() {
        return damageRecords.stream()
                .mapToDouble(DamageRecord::getDamageAmount)
                .sum();
    }

    // 伤害
    @Override public void addHurtRecord(HurtRecord newRecord) {
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
    @Override public void addDamageRecord(DamageRecord newRecord) {
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
    @Override public void addKnockRecord(KnockRecord newRecord) {
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
    @Override public void addDownRecord(DownRecord newRecord) {
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
    @Override public void addReviveRecord(ReviveRecord newRecord) {
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
    @Override public void addKillRecord(KillRecord newRecord) {
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
    @Override public void addDeathRecord(DeathRecord newRecord) {
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

    @Override public @Nullable HurtRecord copyLastHurtRecord() {
        return hurtRecords.isEmpty() ? null : hurtRecords.get(hurtRecords.size() - 1).copyRecord();
    }
    @Override public @Nullable DamageRecord copyLastDamageRecord() {
        return damageRecords.isEmpty() ? null : damageRecords.get(damageRecords.size() - 1).copyRecord();
    }
    @Override public @Nullable KnockRecord copyLastKnockRecord() {
        return knockRecords.isEmpty() ? null : knockRecords.get(knockRecords.size() - 1).copyRecord();
    }
    @Override public @Nullable DownRecord copyLastDownRecord() {
        return downRecords.isEmpty() ? null : downRecords.get(downRecords.size() - 1).copyRecord();
    }
    @Override public @Nullable ReviveRecord copyLastReviveRecord() {
        return reviveRecords.isEmpty() ? null : reviveRecords.get(reviveRecords.size() - 1).copyRecord();
    }
    @Override public @Nullable KillRecord copyLastKillRecord() {
        return killRecords.isEmpty() ? null : killRecords.get(killRecords.size() - 1).copyRecord();
    }
    @Override public @Nullable DeathRecord copyLastDeathRecord() {
        return deathRecords.isEmpty() ? null : deathRecords.get(deathRecords.size() - 1).copyRecord();
    }

    @Override public List<HurtRecord> getHurtRecords() {
        return new ArrayList<>(hurtRecords);
    }
    @Override public List<DamageRecord> getDamageRecords() {
        return new ArrayList<>(damageRecords);
    }
    @Override public List<KnockRecord> getKnockRecords() {
        return new ArrayList<>(knockRecords);
    }
    @Override public List<DownRecord> getDownRecords() {
        return new ArrayList<>(downRecords);
    }
    @Override public List<ReviveRecord> getReviveRecords() {
        return new ArrayList<>(reviveRecords);
    }
    @Override public List<KillRecord> getKillRecords() {
        return new ArrayList<>(killRecords);
    }
    @Override public List<DeathRecord> getDeathRecords() {
        return new ArrayList<>(deathRecords);
    }
}