package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.SafeFunc;

public class SafeFuncEntry implements IZoneFuncEntry {

    private final double damage;
    private final int moveDelay;
    private final int moveTime;

    public SafeFuncEntry(double damage, int moveDelay, int moveTime) {
        this.damage = damage;
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
    }

    @Override
    public String getType() {
        return ZoneFuncTag.SAFE;
    }

    @Override
    public ZoneFuncType getZoneFuncType() {
        return ZoneFuncType.SAFE;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new SafeFunc(damage, moveDelay, moveTime);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.DAMAGE, damage);
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        return jsonObject;
    }

    public static SafeFuncEntry fromJson(JsonObject jsonObject) {
        double damage = jsonObject.has(ZoneFuncTag.DAMAGE) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.DAMAGE).getAsDouble() : 0;
        int moveDelay = jsonObject.has(ZoneFuncTag.MOVE_DELAY) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_DELAY).getAsInt() : 0;
        int moveTime = jsonObject.has(ZoneFuncTag.MOVE_TIME) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_TIME).getAsInt() : 0;
        return new SafeFuncEntry(damage, moveDelay, moveTime);
    }
}
