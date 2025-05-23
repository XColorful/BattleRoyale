package xiao.battleroyale.api.game.zone.shape;

import xiao.battleroyale.api.game.zone.IZoneData;
import xiao.battleroyale.api.game.zone.IZoneEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;

import java.util.function.Supplier;

public interface IZoneShapeEntry extends IZoneEntry {
    
    @Override
    IZoneData generateZoneData(Supplier<Float> random);

    StartEntry getStartEntry();

    EndEntry getEndEntry();
}