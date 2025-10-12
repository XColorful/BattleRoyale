package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;

public abstract class AbstractFuncEntry implements IZoneFuncEntry {

    public int moveDelay;
    public int moveTime;
    public int tickFreq;
    public int tickOffset;

    public AbstractFuncEntry(int moveDelay, int moveTime, int funcFreq, int funcOffset) {
        this.moveDelay = Math.max(moveDelay, 0);
        this.moveTime = Math.max(moveTime, 0);
        this.tickFreq = Math.max(funcFreq, 1);
        this.tickOffset = Math.max(funcOffset, -1);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        jsonObject.addProperty(ZoneFuncTag.TICK_FREQUENCY, tickFreq);
        jsonObject.addProperty(ZoneFuncTag.TICK_OFFSET, tickOffset);
        return jsonObject;
    }
}
