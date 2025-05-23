package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.IZoneData;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;

import java.util.function.Supplier;

public class AirdropFuncEntry implements IZoneFuncEntry {

    private final double damage = 0;
    private int moveDelay;
    private int moveTime;

    public AirdropFuncEntry(int moveDelay, int moveTime) {
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
    }

    @Override
    public String getType() {
        return ZoneFuncTag.AIRDROP;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        return jsonObject;
    }


    public static AirdropFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = jsonObject.has(ZoneFuncTag.MOVE_DELAY) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_DELAY).getAsInt() : 0;
        int moveTime = jsonObject.has(ZoneFuncTag.MOVE_TIME) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.TYPE_NAME).getAsInt() : 0;
        return new AirdropFuncEntry(moveDelay, moveTime);
    }

    @Override
    public IZoneData generateZoneData(Supplier<Float> random) {
        return null;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public int getMoveDelay() {
        return moveDelay;
    }

    @Override
    public int getMoveTime() {
        return moveTime;
    }
}
