package xiao.battleroyale.api.game.stats;

import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.common.game.stats.event.*;

@ApiStatus.Internal
public interface IGameEventRecorder {

    void addHurtRecord(HurtRecord hurtRecord);
    void addDamageRecord(DamageRecord damageRecord);
    void addKnockRecord(KnockRecord knockRecord);
    void addDownRecord(DownRecord downRecord);
    void addReviveRecord(ReviveRecord reviveRecord);
    void addKillRecord(KillRecord killRecord);
    void addDeathRecord(DeathRecord deathRecord);
}
