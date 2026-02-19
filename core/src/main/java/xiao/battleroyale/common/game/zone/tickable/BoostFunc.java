package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.effect.IEffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public class BoostFunc extends AbstractSimpleFunc {

    public final int boost;

    public BoostFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, int boost) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.boost = boost;
    }

    @Override
    public void playerFunc(@NotNull ServerLevel serverLevel, GamePlayer gamePlayer) {
        IEffectManager effectManager = BattleRoyale.getEffectManager();
        effectManager.addBoost(gamePlayer.getPlayerUUID(), boost, serverLevel);
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.BOOST;
    }
}