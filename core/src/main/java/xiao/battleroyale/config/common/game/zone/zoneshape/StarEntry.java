package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.StarShape;
import xiao.battleroyale.util.JsonUtils;

public class StarEntry extends AbstractSimpleEntry {

    public int segments;

    public StarEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape, int segments) {
        super(startEntry, endEntry, badShape);
        this.segments = segments;
    }
    @Override public @NotNull StarEntry copy() {
        return new StarEntry(startEntry.copy(), endEntry.copy(), badShape, segments);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.STAR;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.STAR;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new StarShape(startEntry.copy(), endEntry.copy(), badShape, segments);
    }

    @Nullable
    public static StarEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for StarEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry or endEntry for StarEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        int segments = JsonUtils.getJsonInt(jsonObject, ZoneShapeTag.SEGMENTS, 5);
        if (segments < 2) {
            return null;
        }

        return new StarEntry(startEntry, endEntry, badShape, segments);
    }
}