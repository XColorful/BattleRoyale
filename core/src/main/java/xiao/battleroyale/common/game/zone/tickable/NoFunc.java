package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public class NoFunc extends AbstractSimpleFunc {

    public NoFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public void playerFunc(@NotNull ServerLevel serverLevel, GamePlayer gamePlayer) {
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.NO_FUNC;
    }
}
