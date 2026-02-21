package xiao.battleroyale.api.client.render.level;

import xiao.battleroyale.api.client.render.IClientSubRenderer;

public interface IClientSpectateRenderer extends IClientSimpleZoneRenderer, IClientSubRenderer {

    void setEnableSpectateRender(boolean bool);

    void setUseClientColor(boolean use);

    void setClientColorString(String colorString);

    void setRenderBeacon(boolean bool);

    void setRenderBoundingBox(boolean bool);

    void setTransparency(float a);

    void setScanFrequency(int frequency);

    int getScanFrequency();

    void scanSpectatePlayers();
}
