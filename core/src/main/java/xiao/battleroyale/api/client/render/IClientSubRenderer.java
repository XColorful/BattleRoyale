package xiao.battleroyale.api.client.render;

import xiao.battleroyale.api.common.ISideOnly;

public interface IClientSubRenderer extends ISideOnly {

    @Override default boolean clientSideOnly() {
        return true;
    }

    String getRendererName();

    boolean registerRenderEventHandler();

    boolean unregisterRenderEventHandler();
}
