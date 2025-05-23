package xiao.battleroyale.api.game.zone;

import xiao.battleroyale.api.IConfigEntry;

import java.util.function.Supplier;

public interface IZoneEntry extends IConfigEntry {

    IZoneData generateZoneData(Supplier<Float> random);
}
