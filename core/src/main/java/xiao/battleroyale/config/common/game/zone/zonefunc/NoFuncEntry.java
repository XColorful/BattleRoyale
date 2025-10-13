package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.NoFunc;
import xiao.battleroyale.util.JsonUtils;

public class NoFuncEntry extends AbstractFuncEntry {

    public NoFuncEntry(int moveDelay, int moveTime) {
        super(moveDelay, moveTime, Integer.MAX_VALUE / 2,0);
    }
    @Override public @NotNull NoFuncEntry copy() {
        return new NoFuncEntry(moveDelay, moveTime);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.NO_FUNC;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new NoFunc(moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);

        return jsonObject;
    }

    public static NoFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);

        return new NoFuncEntry(moveDelay, moveTime);
    }
}
