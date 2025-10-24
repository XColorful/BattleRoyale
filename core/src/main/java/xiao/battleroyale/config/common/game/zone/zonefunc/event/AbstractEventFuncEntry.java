package xiao.battleroyale.config.common.game.zone.zonefunc.event;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.config.common.game.zone.zonefunc.AbstractFuncEntry;

public abstract class AbstractEventFuncEntry extends AbstractFuncEntry {

    public String protocol;
    public @NotNull JsonObject jsonTag;

    public AbstractEventFuncEntry(int moveDelay, int moveTime, int funcFreq, int funcOffset,
                                  String protocol, @Nullable JsonObject jsonTag) {
        super(moveDelay, moveTime, funcFreq, funcOffset);
        this.protocol = protocol;
        this.jsonTag = jsonTag != null ? jsonTag : new JsonObject();
    }
}
