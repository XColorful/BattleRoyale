package xiao.battleroyale.api.client.render.level;

import xiao.battleroyale.api.client.render.IClientSubRenderer;

public interface IClientTeamRenderer extends IClientSimpleZoneRenderer, IClientSubRenderer {

    void setEnableTeamZone(boolean bool);

    void setUseClientColor(boolean use);

    void setClientColorString(String colorString);

    void setRenderBeacon(boolean bool);

    void setRenderBoundingBox(boolean bool);

    void setTransparency(float a);
}
