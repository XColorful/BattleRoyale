package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.CubeShape;

import javax.annotation.Nullable;

public class CubeEntry extends AbstractSimpleEntry {

    public CubeEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }
    @Override public @NotNull CubeEntry copy() {
        return new CubeEntry(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.CUBE;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.CUBE;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new CubeShape(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Nullable
    public static CubeEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for CubeEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry or endEntry for CubeEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        return new CubeEntry(startEntry, endEntry, badShape);
    }
}
