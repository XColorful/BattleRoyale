package xiao.battleroyale.api.game.bot;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;

public interface IBotEntry extends IConfigSingleEntry {

    @Override
    @NotNull
    IBotEntry copy();
}
