package xiao.battleroyale.config.client.render.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IRenderEntry;
import xiao.battleroyale.api.client.render.RenderConfigTag;
import xiao.battleroyale.client.renderer.game.TeamMemberRenderer;
import xiao.battleroyale.util.JsonUtils;

public class TeamEntry implements IRenderEntry {

    public boolean enableTeamZone;
    public boolean useClientColor;
    public String fixedColor;
    public boolean renderBeacon;
    public boolean renderBoundingBox;
    public float transparency;

    public TeamEntry(boolean enableTeamZone, boolean useClientColor) {
        this(enableTeamZone, useClientColor, "#00FFFF",
                true, true, 0.5F);
    }
    public TeamEntry(boolean enableTeamZone, boolean useClientColor, String fixedColor,
                     boolean renderBeacon, boolean renderBoundingBox, float transparency) {
        this.enableTeamZone = enableTeamZone;
        this.useClientColor = useClientColor;
        this.fixedColor = fixedColor;
        this.renderBeacon = renderBeacon;
        this.renderBoundingBox = renderBoundingBox;
        this.transparency = transparency;
    }
    @Override public @NotNull TeamEntry copy() {
        return new TeamEntry(enableTeamZone, useClientColor, fixedColor,
                renderBeacon, renderBoundingBox, transparency);
    }

    @Override
    public String getType() {
        return "TeamEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RenderConfigTag.ENABLE_TEAM_ZONE, enableTeamZone);
        if (!enableTeamZone) {
            return jsonObject;
        }
        jsonObject.addProperty(RenderConfigTag.USE_CLIENT_COLOR, useClientColor);
        if (useClientColor) {
            jsonObject.addProperty(RenderConfigTag.FIXED_COLOR, fixedColor);
        }
        jsonObject.addProperty(RenderConfigTag.RENDER_BEACON, renderBeacon);
        jsonObject.addProperty(RenderConfigTag.RENDER_BOUNDING_BOX, renderBoundingBox);
        jsonObject.addProperty(RenderConfigTag.TRANSPARENCY, transparency);

        return jsonObject;
    }

    @NotNull
    public static TeamEntry fromJson(JsonObject jsonObject) {
        boolean enableTeamZone = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.ENABLE_TEAM_ZONE, true);
        boolean useClientColor = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.USE_CLIENT_COLOR, false);
        String fixedColor = JsonUtils.getJsonString(jsonObject, RenderConfigTag.FIXED_COLOR, "");
        boolean headBeacon = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.RENDER_BEACON, true);
        boolean boundingBox = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.RENDER_BOUNDING_BOX, true);
        float transparency = (float) JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.TRANSPARENCY, 0.5);

        return new TeamEntry(enableTeamZone, useClientColor, fixedColor, headBeacon, boundingBox, transparency);
    }

    @Override
    public void applyDefault() {
        TeamMemberRenderer.setEnableTeamZone(enableTeamZone);
        if (enableTeamZone) {
            TeamMemberRenderer.setUseClientColor(useClientColor);
            if (useClientColor) {
                TeamMemberRenderer.setClientColorString(fixedColor);
            }
            TeamMemberRenderer.setRenderBeacon(renderBeacon);
            TeamMemberRenderer.setRenderBoundingBox(renderBoundingBox);
            TeamMemberRenderer.setTransparency(transparency);
        }
    }
}
