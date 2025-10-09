package xiao.battleroyale.config.client.render.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IRenderEntry;
import xiao.battleroyale.api.client.render.RenderConfigTag;
import xiao.battleroyale.client.renderer.game.SpectatePlayerRenderer;
import xiao.battleroyale.util.JsonUtils;

public class SpectateEntry implements IRenderEntry {

    public boolean enableSpectateRender;
    public boolean useClientColor;
    public String fixedColor;
    public boolean renderBeacon;
    public boolean renderBoundingBox;
    public float transparency;
    public int scanFrequency;

    public SpectateEntry(boolean enableSpectateRender, boolean useClientColor) {
        this(enableSpectateRender, useClientColor, "#00FFFF",
                true, true, 0.5F, 60);
    }

    public SpectateEntry(boolean enableSpectateRender, boolean useClientColor, String fixedColor,
                         boolean renderBeacon, boolean renderBoundingBox, float transparency, int scanFrequency) {
        this.enableSpectateRender = enableSpectateRender;
        this.useClientColor = useClientColor;
        this.fixedColor = fixedColor;
        this.renderBeacon = renderBeacon;
        this.renderBoundingBox = renderBoundingBox;
        this.transparency = transparency;
        this.scanFrequency = scanFrequency;
    }

    @Override
    public String getType() {
        return "SpectateEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RenderConfigTag.ENABLE_SPECTATE_ZONE, enableSpectateRender);
        if (!enableSpectateRender) {
            return jsonObject;
        }
        jsonObject.addProperty(RenderConfigTag.USE_CLIENT_COLOR, useClientColor);
        if (useClientColor) {
            jsonObject.addProperty(RenderConfigTag.FIXED_COLOR, fixedColor);
        }
        jsonObject.addProperty(RenderConfigTag.RENDER_BEACON, renderBeacon);
        jsonObject.addProperty(RenderConfigTag.RENDER_BOUNDING_BOX, renderBoundingBox);
        jsonObject.addProperty(RenderConfigTag.TRANSPARENCY, transparency);
        jsonObject.addProperty(RenderConfigTag.SCAN_FREQUENCY, scanFrequency);
        return jsonObject;
    }

    @NotNull
    public static SpectateEntry fromJson(JsonObject jsonObject) {
        boolean enableSpectateRender = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.ENABLE_SPECTATE_ZONE, true);
        boolean useClientColor = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.USE_CLIENT_COLOR, false);
        String fixedColor = JsonUtils.getJsonString(jsonObject, RenderConfigTag.FIXED_COLOR, "");
        boolean renderBeacon = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.RENDER_BEACON, true);
        boolean renderBoundingBox = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.RENDER_BOUNDING_BOX, true);
        float transparency = (float) JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.TRANSPARENCY, 0.5);
        int scanFrequency = JsonUtils.getJsonInt(jsonObject, RenderConfigTag.SCAN_FREQUENCY, 60);

        return new SpectateEntry(enableSpectateRender, useClientColor, fixedColor,
                renderBeacon, renderBoundingBox, transparency, scanFrequency);
    }

    @Override
    public void applyDefault() {
        SpectatePlayerRenderer.setEnableSpectateRender(enableSpectateRender);
        if (enableSpectateRender) {
            SpectatePlayerRenderer.setUseClientColor(useClientColor);
            if (useClientColor) {
                SpectatePlayerRenderer.setClientColorString(fixedColor);
            }
            SpectatePlayerRenderer.setRenderBeacon(renderBeacon);
            SpectatePlayerRenderer.setRenderBoundingBox(renderBoundingBox);
            SpectatePlayerRenderer.setTransparency(transparency);
            SpectatePlayerRenderer.setScanFrequency(scanFrequency);
        }
    }
}