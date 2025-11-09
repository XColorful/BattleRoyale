package xiao.battleroyale.api.client.render.game.level;

public interface IClientTeamRenderer extends IClientTranslucentRender {

    void setEnableTeamZone(boolean bool);

    void setUseClientColor(boolean use);

    void setClientColorString(String colorString);

    void setRenderBeacon(boolean bool);

    void setRenderBoundingBox(boolean bool);

    void setTransparency(float a);
}
