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
    public float itemRenderScale;
    public float itemRenderHeight;
    public boolean doItemBobbing;
    public boolean doItemSpinning;
    public float itemBobPhase;
    public float itemBobSpeed;
    public float itemBobHeight;
    public float itemSpinSpeed;
    public double entityRenderDistance;
    public boolean enableFrustumCulling;
    public boolean enableOcclusionCulling;
    public long occlusionCheckInterval;

    public BlockEntry(double lootRenderDistance, boolean renderItemBlockIfEmpty, double entityRenderDistance) {
        this(lootRenderDistance, renderItemBlockIfEmpty,
                RenderConfigTag.VANILLA_ITEM_RENDER_SCALE, RenderConfigTag.VANILLA_ITEM_RENDER_HEIGHT, true, true,
                RenderConfigTag.VANILLA_BOB_PHASE, RenderConfigTag.VANILLA_BOB_SPEED, RenderConfigTag.VANILLA_BOB_HEIGHT, RenderConfigTag.VANILLA_SPIN_SPEED,
                entityRenderDistance,
                true, true, 500);
    }
    public BlockEntry(double lootRenderDistance, boolean renderItemBlockIfEmpty,
                      float itemRenderScale, float itemRenderHeight, boolean doItemBobbing, boolean doItemSpinning,
                      float itemBobPhase, float itemBobSpeed, float itemBobHeight, float itemSpinSpeed,
                      double entityRenderDistance,
                      boolean enableFrustumCulling, boolean enableOcclusionCulling, long occlusionCheckInterval) {
        this.lootRenderDistance = lootRenderDistance;
        this.renderItemBlockIfEmpty = renderItemBlockIfEmpty;
        this.itemRenderScale = itemRenderScale;
        this.itemRenderHeight = itemRenderHeight;
        this.doItemBobbing = doItemBobbing;
        this.doItemSpinning = doItemSpinning;
        this.itemBobPhase = itemBobPhase;
        this.itemBobSpeed = itemBobSpeed;
        this.itemBobHeight = itemBobHeight;
        this.itemSpinSpeed = itemSpinSpeed;
        this.entityRenderDistance = entityRenderDistance;
        this.enableFrustumCulling = enableFrustumCulling;
        this.enableOcclusionCulling = enableOcclusionCulling;
        this.occlusionCheckInterval = occlusionCheckInterval;
    }
    @Override public @NotNull BlockEntry copy() {
        return new BlockEntry(lootRenderDistance, renderItemBlockIfEmpty,
                itemRenderScale, itemRenderHeight, doItemBobbing, doItemSpinning,
                itemBobPhase, itemBobSpeed, itemBobHeight, itemSpinSpeed,
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
        jsonObject.addProperty(RenderConfigTag.ITEM_RENDER_SCALE, itemRenderScale);
        jsonObject.addProperty(RenderConfigTag.ITEM_RENDER_HEIGHT, itemRenderHeight);
        jsonObject.addProperty(RenderConfigTag.DO_ITEM_BOBBING, doItemBobbing);
        jsonObject.addProperty(RenderConfigTag.DO_ITEM_SPINNING, doItemSpinning);
        jsonObject.addProperty(RenderConfigTag.ITEM_BOB_PHASE, itemBobPhase);
        jsonObject.addProperty(RenderConfigTag.ITEM_BOB_SPEED, itemBobSpeed);
        jsonObject.addProperty(RenderConfigTag.ITEM_BOB_HEIGHT, itemBobHeight);
        jsonObject.addProperty(RenderConfigTag.ITEM_SPIN_SPEED, itemSpinSpeed);
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
        float itemRenderScale = (float) JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_RENDER_SCALE, RenderConfigTag.VANILLA_ITEM_RENDER_SCALE);
        float itemRenderHeight = (float) JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_RENDER_HEIGHT, RenderConfigTag.VANILLA_ITEM_RENDER_HEIGHT);
        boolean doItemBobbing = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.DO_ITEM_BOBBING, true);
        boolean doItemSpinning = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.DO_ITEM_SPINNING, true);
        float itemBobPhase = (float) JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_BOB_PHASE, RenderConfigTag.VANILLA_BOB_PHASE);
        float itemBobSpeed = (float) JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_BOB_SPEED, RenderConfigTag.VANILLA_BOB_SPEED);
        float itemBobHeight = (float) JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_BOB_HEIGHT, RenderConfigTag.VANILLA_BOB_HEIGHT);
        float itemSpinSpeed = (float) JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ITEM_SPIN_SPEED, RenderConfigTag.VANILLA_SPIN_SPEED);
        double entityRenderDistance = JsonUtils.getJsonDouble(jsonObject, RenderConfigTag.ENTITY_RENDER_DISTANCE, 16);
        boolean enableFrustumCulling = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.FRUSTUM_CULLING, true);
        boolean enableOcclusionCulling = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.OCCLUSION_CULLING, true);
        long occlusionCheckInterval = JsonUtils.getJsonInt(jsonObject, RenderConfigTag.OCCLUSION_CHECK_INTERVAL, 500);

        return new BlockEntry(lootRenderDistance, renderItemBlockIfEmpty,
                itemRenderScale, itemRenderHeight, doItemBobbing, doItemSpinning,
                itemBobPhase, itemBobSpeed, itemBobHeight, itemSpinSpeed,
                entityRenderDistance, enableFrustumCulling, enableOcclusionCulling, occlusionCheckInterval);
    }

    @Override
    public void applyDefault() {
        LootContainerRenderer.setRenderDistance(lootRenderDistance);
        LootContainerRenderer.setRenderIfEmpty(renderItemBlockIfEmpty);
        LootContainerRenderer.setItemRenderScale(itemRenderScale);
        LootContainerRenderer.setItemRenderHeight(itemRenderHeight);
        LootContainerRenderer.setDoBobbing(doItemBobbing);
        LootContainerRenderer.setDoSpinning(doItemSpinning);
        LootContainerRenderer.setAnimationParams(itemBobPhase, itemBobSpeed, itemBobHeight, itemSpinSpeed);
        EntitySpawnerRenderer.setRenderDistance(entityRenderDistance);
        LootContainerRenderer.setDoFrustumCheck(enableFrustumCulling);
        LootContainerRenderer.setDoOcclusionCheck(enableOcclusionCulling);
        LootContainerRenderer.setCheckInterval(occlusionCheckInterval);
    }
}
