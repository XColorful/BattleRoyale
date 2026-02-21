package xiao.battleroyale.api.game.stats;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.stats.record.*;

import java.util.List;

public interface IGameEventRecordGetter {

    @Nullable HurtRecord copyLastHurtRecord();
    @Nullable DamageRecord copyLastDamageRecord();
    @Nullable KnockRecord copyLastKnockRecord();
    @Nullable DownRecord copyLastDownRecord();
    @Nullable ReviveRecord copyLastReviveRecord();
    @Nullable KillRecord copyLastKillRecord();
    @Nullable DeathRecord copyLastDeathRecord();

    /**
     * 仅 IStatsManager 操作，否则只读不修改
     */
    @ApiStatus.Internal List<HurtRecord> getHurtRecords();
    @ApiStatus.Internal List<DamageRecord> getDamageRecords();
    @ApiStatus.Internal List<KnockRecord> getKnockRecords();
    @ApiStatus.Internal List<DownRecord> getDownRecords();
    @ApiStatus.Internal List<ReviveRecord> getReviveRecords();
    @ApiStatus.Internal List<KillRecord> getKillRecords();
    @ApiStatus.Internal List<DeathRecord> getDeathRecords();
}
