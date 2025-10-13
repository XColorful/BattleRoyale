package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.RectangleShape;

public class RectangleEntry extends AbstractSimpleEntry {

    public RectangleEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }
    @Override public @NotNull RectangleEntry copy() {
        return new RectangleEntry(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.RECTANGLE;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.RECTANGLE;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new RectangleShape(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Nullable
    public static RectangleEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for RectangleEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid endEntry for RectangleEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);
        
        return new RectangleEntry(startEntry, endEntry, badShape);
    }
}