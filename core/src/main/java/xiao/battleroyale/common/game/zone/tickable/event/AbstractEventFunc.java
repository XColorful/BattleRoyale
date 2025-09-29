package xiao.battleroyale.common.game.zone.tickable.event;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.zone.tickable.AbstractSimpleFunc;

public abstract class AbstractEventFunc extends AbstractSimpleFunc {

    protected final String protocol;
    protected @NotNull final CompoundTag tag;

    public AbstractEventFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                             String protocol, @NotNull CompoundTag tag) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.protocol = protocol;
        this.tag = tag;
    }
}
