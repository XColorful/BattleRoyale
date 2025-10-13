package xiao.battleroyale.api.client.render;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.sub.IConfigAppliable;
import xiao.battleroyale.api.config.sub.IConfigEntry;

public interface IRenderEntry extends IConfigEntry, IConfigAppliable {

    @Override
    @NotNull
    IRenderEntry copy();
}
