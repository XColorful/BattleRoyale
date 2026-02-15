package xiao.battleroyale.common.game.stats.event;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.stats.IGamePlayerRecord;
import xiao.battleroyale.common.game.team.GamePlayer;

public abstract class AbstractGamePlayerEventRecord<T extends AbstractGamePlayerEventRecord<T>>
        extends AbstractGameEventRecord<T>
        implements IGamePlayerRecord {

    protected @NotNull GamePlayer gamePlayer;
    protected final boolean causeByGamePlayer;
    protected @Nullable GamePlayer causeGamePlayer;
    protected @Nullable DamageSource causeDamageSource;

    protected AbstractGamePlayerEventRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @NotNull GamePlayer causeGamePlayer) {
        this(gameTime, timeOrder, gamePlayer, true, causeGamePlayer, null);
    }
    protected AbstractGamePlayerEventRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @Nullable DamageSource causeDamageSource) {
        this(gameTime, timeOrder, gamePlayer, false, null, causeDamageSource);
    }
    private AbstractGamePlayerEventRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer,
                                          boolean causeByGamePlayer, @Nullable GamePlayer causeGamePlayer, @Nullable DamageSource causeDamageSource) {
        super(gameTime, timeOrder);
        this.gamePlayer = gamePlayer;
        this.causeByGamePlayer = causeByGamePlayer;
        this.causeGamePlayer = causeGamePlayer;
        this.causeDamageSource = causeDamageSource;
    }

    @Override
    public boolean canStack(T newRecord) {
        if (isCauseByGamePlayer()) {
            return newRecord.isCauseByGamePlayer() && causeGamePlayer == newRecord.gamePlayer;
        } else {
            // 简化处理
            if (causeDamageSource != null && newRecord.causeDamageSource != null) { // 同时有
                return causeDamageSource.toString().equals(newRecord.causeDamageSource.toString());
            } else if (causeDamageSource == null && newRecord.causeDamageSource == null) { // 同时没有
                return true;
            } else { // 两两不等
                return false;
            }
        }
    }

    @Override public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public boolean isCauseByGamePlayer() {
        return this.causeByGamePlayer;
    }
}
