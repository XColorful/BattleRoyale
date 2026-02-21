package xiao.battleroyale.common.game.stats.record;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 死亡事件的记录
 */
public class DeathRecord extends AbstractGamePlayerEventRecord<DeathRecord> {

    private int deathCount = 1;

    public DeathRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @NotNull GamePlayer causeGamePlayer) {
        super(gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }
    public DeathRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @Nullable DamageSource causeDamageSource) {
        super(gameTime, timeOrder, gamePlayer, causeDamageSource);
    }

    public int getDeathCount() {
        return deathCount;
    }

    @Override
    public boolean stackAdditional(DeathRecord newRecord) {
        return true;
    }

    @Override
    public DeathRecord copyRecord() {
        return isCauseByGamePlayer()
                ? new DeathRecord(gameTime, timeOrder, gamePlayer, causeGamePlayer)
                : new DeathRecord(gameTime, timeOrder, gamePlayer, causeDamageSource);
    }
}