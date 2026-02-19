package xiao.battleroyale.common.game.zone.tickable.event;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.tickable.AbstractSimpleFunc;

public abstract class AbstractEventFunc extends AbstractSimpleFunc {

    protected final String protocol;
    protected @NotNull final JsonObject jsonTag;

    public AbstractEventFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                             String protocol, @NotNull JsonObject jsonTag) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.protocol = protocol;
        this.jsonTag = jsonTag;
    }

    @Override
    public void playerFunc(@NotNull ServerLevel serverLevel, GamePlayer gamePlayer) {
    }
}
