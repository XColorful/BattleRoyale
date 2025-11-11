package xiao.battleroyale.api.client.render.game.level;

public interface IClientZoneRenderer extends IClientSimpleZoneRenderer {

    int getCircleSegments();
    void setCircleSegments(int segments);

    int getEllipseSegments();
    void setEllipseSegments(int segments);

    int getSphereSegments();
    void setSphereSegments(int segments);

    int getEllipsoidSegments();
    void setEllipsoidSegments(int segments);
}
