package xiao.battleroyale.config.client.render.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.client.render.IRenderEntry;
import xiao.battleroyale.api.config.client.render.RenderConfigTag;
import xiao.battleroyale.client.renderer.block.EntitySpawnerRenderer;
import xiao.battleroyale.client.renderer.block.LootContainerRenderer;
import xiao.battleroyale.util.JsonUtils;

public class BlockEntry implements IRenderEntry {

    public double lootRenderDistance;
    public boolean renderItemBlockIfEmpty;
    public double entityRenderDistance;
    public boolean enableFrustumCulling;
    public boolean enableOcclusionCulling;
    public long occlusionCheckInterval;

    public BlockEntry(double lootRenderDistance, boolean renderItemBlockIfEmpty, double entityRenderDistance) {
        this(lootRenderDistance, renderItemBlockIfEmpty, entityRenderDistance, true, true, 500);
    }
    public BlockEntry(double lootRenderDistance, boolean renderItemBlockIfEmpty,
                      double entityRenderDistance,
                      boolean enableFrustumCulling, boolean enableOcclusionCulling, long occlusionCheckInterval) {
        this.lootRenderDistance = lootRenderDistance;
        this.renderItemBlockIfEmpty = renderItemBlockIfEmpty;
        this.entityRenderDistance = entityRenderDistance;
        this.enableFrustumCulling = enableFrustumCulling;
        this.enableOcclusionCulling = enableOcclusionCulling;
        this.occlusionCheckInterval = occlusionCheckInterval;
    }
    @Override public @NotNull BlockEntry copy() {
        return new BlockEntry(lootRenderDistance, renderItemBlockIfEmpty,
                entityRenderDistance,
                enableFrustumCulling, enableOcclusionCulling, occlusionCheckInterval);
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
        jsonObject.addProperty(RenderConfigTag.FRUSTUM_CULLING, enableFrustumCulling);
        jsonObject.addProperty(RenderConfigTag.OCCLUSION_CULLING, enableOcclusionCulling);
        jsonObject.addProperty(RenderConfigTag.OCCLUSION_CHECK_INTERVAL, occlusionCheckInterval);

        return jsonObject;
    }

    @NotNull
    public static BlockEntry fromJson(JsonObject jsonObject) {
        double lootRenderDistance = JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_RENDER_DISTANCE, 16);
        boolean renderItemBlockIfEmpty = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.RENDER_ITEM_BLOCK_IF_EMPTY, true);
        double entityRenderDistance = JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ENTITY_RENDER_DISTANCE, 16);
        boolean enableFrustumCulling = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.FRUSTUM_CULLING, true);
        boolean enableOcclusionCulling = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.OCCLUSION_CULLING, true);
        long occlusionCheckInterval = JsonUtils.getJsonInt(jsonObject, RenderConfigTag.OCCLUSION_CHECK_INTERVAL, 500);

        return new BlockEntry(lootRenderDistance, renderItemBlockIfEmpty, entityRenderDistance, enableFrustumCulling, enableOcclusionCulling, occlusionCheckInterval);
    }

    @Override
    public void applyDefault() {
        LootContainerRenderer.setRenderDistance(lootRenderDistance);
        LootContainerRenderer.setRenderIfEmpty(renderItemBlockIfEmpty);
        EntitySpawnerRenderer.setRenderDistance(entityRenderDistance);
        LootContainerRenderer.setDoFrustumCheck(enableFrustumCulling);
        LootContainerRenderer.setDoOcclusionCheck(enableOcclusionCulling);
        LootContainerRenderer.setCheckInterval(occlusionCheckInterval);
    }
}
