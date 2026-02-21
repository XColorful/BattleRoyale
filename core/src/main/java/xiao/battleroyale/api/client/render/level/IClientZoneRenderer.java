package xiao.battleroyale.api.client.render.level;

import xiao.battleroyale.api.client.render.IClientSubRenderer;

public interface IClientZoneRenderer extends IClientSimpleZoneRenderer, IClientSubRenderer {

    int getCircleSegments();
    void setCircleSegments(int segments);

    int getEllipseSegments();
    void setEllipseSegments(int segments);

    int getSphereSegments();
    void setSphereSegments(int segments);

    int getEllipsoidSegments();
    void setEllipsoidSegments(int segments);
}
