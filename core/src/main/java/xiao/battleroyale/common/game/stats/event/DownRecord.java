package xiao.battleroyale.common.game.stats.event;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 倒地事件的记录
 */
public class DownRecord extends AbstractGamePlayerEventRecord<DownRecord> {

    private int downCount = 1;

    public DownRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @NotNull GamePlayer causeGamePlayer) {
        super(gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }
    public DownRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @Nullable DamageSource causeDamageSource) {
        super(gameTime, timeOrder, gamePlayer, causeDamageSource);
    }

    public int getDownCount() {
        return downCount;
    }

    @Override
    public boolean canStack(DownRecord newRecord) {
        return false;
    }

    @Override
    public boolean stackAdditional(DownRecord newRecord) {
        return false;
    }

    @Override
    public DownRecord copyRecord() {
        return isCauseByGamePlayer()
                ? new DownRecord(this.gameTime, timeOrder, gamePlayer, causeGamePlayer)
                : new DownRecord(this.gameTime, timeOrder, gamePlayer, causeDamageSource);
    }
}
