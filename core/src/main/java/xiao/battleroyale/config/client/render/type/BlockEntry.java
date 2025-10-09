package xiao.battleroyale.config.client.render.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IRenderEntry;
import xiao.battleroyale.api.client.render.RenderConfigTag;
import xiao.battleroyale.client.renderer.block.EntitySpawnerRenderer;
import xiao.battleroyale.client.renderer.block.LootContainerRenderer;
import xiao.battleroyale.util.JsonUtils;

public class BlockEntry implements IRenderEntry {

    public double lootRenderDistance;
    public boolean renderItemBlockIfEmpty;
    public double entityRenderDistance;

    public BlockEntry(double lootRenderDistance, boolean renderItemBlockIfEmpty,
                      double entityRenderDistance) {
        this.lootRenderDistance = lootRenderDistance;
        this.renderItemBlockIfEmpty = renderItemBlockIfEmpty;
        this.entityRenderDistance = entityRenderDistance;
    }

    @Override
    public String getType() {
        return "blockEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RenderConfigTag.ITEM_RENDER_DISTANCE, lootRenderDistance);
        jsonObject.addProperty(RenderConfigTag.RENDER_ITEM_BLOCK_IF_EMPTY, renderItemBlockIfEmpty);
        jsonObject.addProperty(RenderConfigTag.ENTITY_RENDER_DISTANCE, entityRenderDistance);

        return jsonObject;
    }

    @NotNull
    public static BlockEntry fromJson(JsonObject jsonObject) {
        double lootRenderDistance = JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_RENDER_DISTANCE, 16);
        boolean renderItemBlockIfEmpty = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.RENDER_ITEM_BLOCK_IF_EMPTY, true);
        double entityRenderDistance = JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ENTITY_RENDER_DISTANCE, 16);

        return new BlockEntry(lootRenderDistance, renderItemBlockIfEmpty, entityRenderDistance);
    }

    @Override
    public void applyDefault() {
        LootContainerRenderer.setRenderDistance(lootRenderDistance);
        LootContainerRenderer.setRenderIfEmpty(renderItemBlockIfEmpty);
        EntitySpawnerRenderer.setRenderDistance(entityRenderDistance);
    }
}
