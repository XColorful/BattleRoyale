package xiao.battleroyale.api.client.render.gui;

import xiao.battleroyale.api.client.render.IClientSubRenderer;

public interface IClientGameInfoRenderer extends IClientGuiRender, IClientSubRenderer {

    void setDisplayAlive(boolean shouldDisplay);

    void setAliveXRatio(double ratio);

    void setAliveYRatio(double ratio);

    void setAliveColor(String colorString);

    void setAliveCountColor(String colorString);
}
