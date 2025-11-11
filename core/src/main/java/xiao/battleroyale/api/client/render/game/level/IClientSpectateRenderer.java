package xiao.battleroyale.api.client.render.game.level;

public interface IClientSpectateRenderer extends IClientSimpleZoneRenderer {

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
