package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.util.JsonUtils;

public abstract class AbstractSimpleEntry implements IZoneShapeEntry {

    protected final StartEntry startEntry;
    protected final EndEntry endEntry;
    protected final boolean badShape;

    public AbstractSimpleEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        this.startEntry = startEntry;
        this.endEntry = endEntry;
        this.badShape = badShape;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneShapeTag.TYPE_NAME, getType());
        jsonObject.add(ZoneShapeTag.START, startEntry.toJson());
        jsonObject.add(ZoneShapeTag.END, endEntry.toJson());
        jsonObject.addProperty(ZoneShapeTag.BAD_SHAPE, badShape);
        return jsonObject;
    }

    public static StartEntry readStartEntry(JsonObject jsonObject) {
        JsonObject startEntryObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.START, null);
        if (startEntryObject == null) {
            BattleRoyale.LOGGER.info("Shape entry missing start or end member, skipped");
            return null;
        }
        StartEntry startEntry = StartEntry.fromJson(startEntryObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry, skipped");
            return null;
        }
        return startEntry;
    }

    public static EndEntry readEndEntry(JsonObject jsonObject) {
        JsonObject endEntryObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.END, null);
        if (endEntryObject == null) {
            return null;
        }
        EndEntry endEntry = EndEntry.fromJson(endEntryObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid endEntry for HexagonEntry, skipped");
            return null;
        }
        return endEntry;
    }

    public static boolean readBadShape(JsonObject jsonObject) {
        return JsonUtils.getJsonBoolean(jsonObject, ZoneShapeTag.BAD_SHAPE, false);
    }
}
