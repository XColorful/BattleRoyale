package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.DangerFunc;

public class DangerFuncEntry extends AbstractFuncEntry {

    public DangerFuncEntry(int moveDelay, int moveTime) {
        super(moveDelay, moveTime);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.DANGER;
    }

    @Override
    public ZoneFuncType getZoneFuncType() {
        return ZoneFuncType.DANGER;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new DangerFunc(moveDelay, moveTime);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        return jsonObject;
    }

    public static DangerFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = jsonObject.has(ZoneFuncTag.MOVE_DELAY) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_DELAY).getAsInt() : 0;
        int moveTime = jsonObject.has(ZoneFuncTag.MOVE_TIME) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_TIME).getAsInt() : 0;
        return new DangerFuncEntry(moveDelay, moveTime);
    }
}
