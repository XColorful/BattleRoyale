package xiao.battleroyale.api.client.render.level;

public interface IClientTeamRenderer extends IClientSimpleZoneRenderer {

    void setEnableTeamZone(boolean bool);

    void setUseClientColor(boolean use);

    void setClientColorString(String colorString);

    void setRenderBeacon(boolean bool);

    void setRenderBoundingBox(boolean bool);

    void setTransparency(float a);
}
