package xiao.battleroyale.api.game.zone.func;

import xiao.battleroyale.api.game.zone.IZoneData;
import xiao.battleroyale.api.game.zone.IZoneEntry;

import java.util.function.Supplier;

public interface IZoneFuncEntry extends IZoneEntry {

    @Override
    IZoneData generateZoneData(Supplier<Float> random);

    double getDamage();

    int getMoveDelay();

    int getMoveTime();
}