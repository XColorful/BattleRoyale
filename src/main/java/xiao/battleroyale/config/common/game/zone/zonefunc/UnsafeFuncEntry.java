package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.UnsafeFunc;
import xiao.battleroyale.util.JsonUtils;

public class UnsafeFuncEntry extends AbstractFuncEntry {

    public UnsafeFuncEntry(double damage, int moveDelay, int moveTime, int tickFreq, int tickOffset) {
        super(damage, moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.UNSAFE;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new UnsafeFunc(damage, moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.DAMAGE, damage);
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        jsonObject.addProperty(ZoneFuncTag.TICK_FREQUENCY, tickFreq);
        jsonObject.addProperty(ZoneFuncTag.TICK_OFFSET, tickOffset);
        return jsonObject;
    }

    public static UnsafeFuncEntry fromJson(JsonObject jsonObject) {
        double damage = JsonUtils.getJsonDouble(jsonObject, ZoneFuncTag.DAMAGE, 0);
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        return new UnsafeFuncEntry(damage, moveDelay, moveTime, tickFreq, tickOffset);
    }
}
