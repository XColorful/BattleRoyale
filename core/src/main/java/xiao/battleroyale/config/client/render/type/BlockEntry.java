package xiao.battleroyale.config.client.render.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IRenderEntry;
import xiao.battleroyale.api.client.render.RenderConfigTag;
import xiao.battleroyale.client.renderer.block.LootContainerRenderer;
import xiao.battleroyale.util.JsonUtils;

public class BlockEntry implements IRenderEntry {

    public final double lootRenderDistance;

    public BlockEntry(double lootRenderDistance) {
        this.lootRenderDistance = lootRenderDistance;
    }

    @Override
    public String getType() {
        return "blockEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RenderConfigTag.ITEM_RENDER_DISTANCE, lootRenderDistance);
        return jsonObject;
    }

    @NotNull
    public static BlockEntry fromJson(JsonObject jsonObject) {
        double lootRenderDistance = JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_RENDER_DISTANCE, 16);

        return new BlockEntry(lootRenderDistance);
    }

    @Override
    public void applyDefault() {
        LootContainerRenderer.setRenderDistance(lootRenderDistance);
    }
}
