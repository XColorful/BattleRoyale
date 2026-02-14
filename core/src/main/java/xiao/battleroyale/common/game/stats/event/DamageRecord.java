package xiao.battleroyale.common.game.stats.event;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 被造成伤害事件的记录
 */
public class DamageRecord extends AbstractGamePlayerEventRecord<DamageRecord> {

    private double damageAmount = 0;

    public DamageRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @NotNull GamePlayer causeGamePlayer, double damageAmount) {
        super(gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }
    public DamageRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @Nullable DamageSource causeDamageSource, double damageAmount) {
        super(gameTime, timeOrder, gamePlayer, causeDamageSource);
    }

    public double getDamageAmount() {
        return damageAmount;
    }

    @Override
    public boolean stackAdditional(DamageRecord newRecord) {
        this.damageAmount += newRecord.damageAmount;
        return true;
    }

    @Override
    public DamageRecord copyRecord() {
        return isCauseByGamePlayer()
                ? new DamageRecord(gameTime, timeOrder, gamePlayer, causeGamePlayer, damageAmount)
                : new DamageRecord(gameTime, timeOrder, gamePlayer, causeDamageSource, damageAmount);
    }
}