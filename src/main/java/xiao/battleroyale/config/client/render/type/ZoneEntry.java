package xiao.battleroyale.config.client.render.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IRenderEntry;
import xiao.battleroyale.api.client.render.RenderConfigTag;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.client.renderer.game.ZoneRenderer;
import xiao.battleroyale.common.game.zone.spatial.CircleShape;
import xiao.battleroyale.common.game.zone.spatial.EllipseShape;
import xiao.battleroyale.common.game.zone.spatial.EllipsoidShape;
import xiao.battleroyale.common.game.zone.spatial.SphereShape;
import xiao.battleroyale.util.JsonUtils;

public class ZoneEntry implements IRenderEntry {

    public final boolean useClientColor;
    public final String fixedColor;
    public final int circleSegments;
    public final int ellipseSegments;
    public final int sphereSegments;
    public final int ellipsoidSegments;

    public ZoneEntry(boolean useClientColor) {
        this(useClientColor, "#0000FF",
                64, 64, 64, 64);
    }

    public ZoneEntry(boolean useClientColor, String fixedColor,
                     int circleSegments, int ellipseSegments, int sphereSegments, int ellipsoidSegments) {
        this.useClientColor = useClientColor;
        this.fixedColor = fixedColor;
        this.circleSegments = circleSegments;
        this.ellipseSegments = ellipseSegments;
        this.sphereSegments = sphereSegments;
        this.ellipsoidSegments = ellipsoidSegments;
    }

    @Override
    public String getType() {
        return "zoneEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RenderConfigTag.USE_CLIENT_COLOR, useClientColor);
        if (useClientColor) {
            jsonObject.addProperty(RenderConfigTag.FIXED_COLOR, fixedColor);
        }
        jsonObject.addProperty(RenderConfigTag.CIRCLE_SEGMENTS, circleSegments);
        jsonObject.addProperty(RenderConfigTag.ELLIPSE_SEGMENTS, ellipseSegments);
        jsonObject.addProperty(RenderConfigTag.SPHERE_SEGMENTS, sphereSegments);
        jsonObject.addProperty(RenderConfigTag.ELLIPSOID_SEGMENTS, ellipsoidSegments);

        return jsonObject;
    }

    @NotNull
    public static ZoneEntry fromJson(JsonObject jsonObject) {
        boolean useClientColor = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.USE_CLIENT_COLOR, false);
        String fixedColor = JsonUtils.getJsonString(jsonObject, RenderConfigTag.FIXED_COLOR, "");
        int circleSegments = JsonUtils.getJsonInt(jsonObject, RenderConfigTag.CIRCLE_SEGMENTS, 64);
        int ellipseSegments = JsonUtils.getJsonInt(jsonObject, RenderConfigTag.ELLIPSE_SEGMENTS, 64);
        int sphereSegments = JsonUtils.getJsonInt(jsonObject, RenderConfigTag.SPHERE_SEGMENTS, 64);
        int ellipsoidSegments = JsonUtils.getJsonInt(jsonObject, RenderConfigTag.ELLIPSOID_SEGMENTS, 64);

        return new ZoneEntry(useClientColor, fixedColor, circleSegments, ellipseSegments, sphereSegments, ellipsoidSegments);
    }

    @Override
    public void applyDefault() {
        ClientSingleZoneData.setUseClientColor(this.useClientColor);
        if (useClientColor) {
            ClientSingleZoneData.setClientColorString(this.fixedColor);
        }
        CircleShape.setCircleSegments(circleSegments);
        ZoneRenderer.setCircleSegments(CircleShape.getCircleSegments());
        EllipseShape.setEllipseSegments(ellipsoidSegments);
        ZoneRenderer.setEllipseSegments(EllipseShape.getEllipseSegments());
        SphereShape.setSphereSegments(sphereSegments);
        ZoneRenderer.setSphereSegments(SphereShape.getSphereSegments());
        EllipsoidShape.setEllipsoidSegments(ellipsoidSegments);
        ZoneRenderer.setEllipsoidSegments(EllipsoidShape.getEllipsoidSegments());
    }
}
