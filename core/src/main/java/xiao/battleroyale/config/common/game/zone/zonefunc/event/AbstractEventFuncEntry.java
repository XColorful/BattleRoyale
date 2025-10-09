package xiao.battleroyale.config.common.game.zone.zonefunc.event;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.config.common.game.zone.zonefunc.AbstractFuncEntry;

public abstract class AbstractEventFuncEntry extends AbstractFuncEntry {

    public String protocol;
    public @NotNull CompoundTag tag;

    public AbstractEventFuncEntry(int moveDelay, int moveTime, int funcFreq, int funcOffset,
                                  String protocol, @Nullable CompoundTag tag) {
        super(moveDelay, moveTime, funcFreq, funcOffset);
        this.protocol = protocol;
        this.tag = tag != null ? tag : new CompoundTag();
    }
}
