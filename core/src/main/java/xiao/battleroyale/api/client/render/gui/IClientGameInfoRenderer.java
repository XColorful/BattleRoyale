package xiao.battleroyale.api.client.render.gui;

public interface IClientGameInfoRenderer extends IClientGuiRender {

    void setDisplayAlive(boolean shouldDisplay);

    void setAliveXRatio(double ratio);

    void setAliveYRatio(double ratio);

    void setAliveColor(String colorString);

    void setAliveCountColor(String colorString);
}
