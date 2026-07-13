package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.HashCommon;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanMaps;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import xiao.battleroyale.api.config.client.render.RenderConfigTag;
import xiao.battleroyale.block.entity.AbstractLootContainerBlockEntity;

import javax.annotation.Nullable;

public abstract class LootContainerRenderer<T extends AbstractLootContainerBlockEntity> extends AbstractBlockRenderer<T, LootRenderState> {

    protected static double MAX_RENDER_DISTANCE_SQ = 16 * 16;
    protected static boolean RENDER_IF_EMPTY = true;
    protected static float ITEM_RENDER_SCALE = 0.5F;
    protected static float ITEM_RENDER_HEIGHT = 0;
    protected static boolean DO_BOBBING = true; // 上下浮动
    protected static boolean DO_SPINNING = true; // 旋转
    protected static float BOB_PHASE = RenderConfigTag.VANILLA_BOB_PHASE; // 设置为 0 则共振
    protected static float BOB_SPEED = RenderConfigTag.VANILLA_BOB_SPEED; // 浮动速度
    protected static float BOB_HEIGHT = RenderConfigTag.VANILLA_BOB_HEIGHT; // 浮动幅度
    protected static float SPIN_SPEED = RenderConfigTag.VANILLA_SPIN_SPEED; // 旋转速度
    public static void setRenderDistance(double distance) {
        MAX_RENDER_DISTANCE_SQ = distance < 0 ? 0 : distance * distance;
    }
    public static void setRenderIfEmpty(boolean bool) {
        RENDER_IF_EMPTY = bool;
    }
    public static void setItemRenderScale(float scale) {
        ITEM_RENDER_SCALE = Math.max(0.001F, scale); // 增加一个极小的 epsilon 防止除零
    }
    public static void setItemRenderHeight(float height) {
        ITEM_RENDER_HEIGHT = height;
    }
    public static void setDoBobbing(boolean bool) {
        DO_BOBBING = bool;
    }
    public static void setDoSpinning(boolean bool) {
        DO_SPINNING = bool;
    }
    public static void setAnimationParams(float bobPhase, float bobSpeed, float bobHeight, float spinSpeed) {
        BOB_PHASE = bobPhase;
        // 浮动速度和旋转速度允许负数
        BOB_SPEED = bobSpeed;
        SPIN_SPEED = spinSpeed;
        // 浮动高度为负数时，可以做出“探头”效果
        BOB_HEIGHT = bobHeight;
    }

    protected static volatile boolean DO_FRUSTUM_CHECK = true;
    public static void setDoFrustumCheck(boolean bool) {
        DO_FRUSTUM_CHECK = bool;
    }

    protected static volatile boolean DO_OCCLUSION_CHECK = true;
    protected static long CHECK_INTERVAL_MS = 500;
    protected static volatile long lastCheckTime = 0; // volatile保证渲染线程可见性
    protected static final Long2BooleanMap checkedPos = Long2BooleanMaps.synchronize(new Long2BooleanOpenHashMap());
    public static void setDoOcclusionCheck(boolean bool) {
        DO_OCCLUSION_CHECK = bool;
        if (!DO_OCCLUSION_CHECK) checkedPos.clear();
    }
    public static void setCheckInterval(long ms) {
        CHECK_INTERVAL_MS = Math.max(50, ms); // 至少 50ms (1 tick) 避免性能爆炸
    }
    @ApiStatus.Internal public static long getLastCheckTime() { // debug
        return lastCheckTime;
    }
    @ApiStatus.Internal public static int getCheckedPosSize() {
        return checkedPos.size();
    }

    protected final ItemModelResolver itemModelResolver;

    public LootContainerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public @NotNull LootRenderState createRenderState() {
        return new LootRenderState(getLootContainerSize());
    }
    protected abstract int getLootContainerSize();

    @Override
    public void render(@NotNull T blockEntity, float partialTick, @NotNull PoseStack poseStack, int combinedLightIn, int combinedOverlayIn,
                       @NotNull LootRenderState renderState, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraState) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 获取摄像机数据
        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 cameraPos = camera.position();
        Vector3fc cameraLook = camera.forwardVector();

        // ---1. 距离判断---
        // 3减 3乘 2加 1比较 (L1 Cache)
        BlockPos pos = blockEntity.getBlockPos();
        double dx = pos.getX() + 0.5 - cameraPos.x;
        double dy = pos.getY() + 0.5 - cameraPos.y;
        double dz = pos.getZ() + 0.5 - cameraPos.z;
        if (dx * dx + dy * dy + dz * dz > MAX_RENDER_DISTANCE_SQ) return;

        // ---2. 夹角判断---
        // shouldRenderOffScreen并没有对方块实体生效
        if (cameraLook.x() * dx + cameraLook.y() * dy + cameraLook.z() * dz < -0.1) return; // 小阈值防止视野边缘闪烁

        // ---3. 状态检查---
        // 逻辑过滤，避免空容器进行后续昂贵的哈希查找和物理检测
        boolean empty = blockEntity.isEmpty();
        if (empty && !RENDER_IF_EMPTY) return;

        // ---4. 视锥体---
        // 剔除视野外的(头顶和楼下)
        if (false && DO_FRUSTUM_CHECK) { // 1.21.1neoforge默认管线已经做了优化
            Frustum frustum = cameraState.cullFrustum;
//            if (!frustum.isVisible(blockEntity.getRenderBoundingBox())) return;
        }
        // --5. 射线检测---
        // 剔除墙后的
        if (false && DO_OCCLUSION_CHECK) { // 1.21.1neoforge默认管线已经做了优化
            boolean isVisible;
            // --- 周期性缓存重置 (Cache Reset) ---
            // 1次 volatile 读 + 1次系统时间调用 (默认每 500ms 降频重置)
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCheckTime > CHECK_INTERVAL_MS) {
                lastCheckTime = currentTime;
                checkedPos.clear();
            }

            // ---遮挡状态获取---
            // 1次 long 转换 + 1次原生 long 哈希查找 (无装箱开销)
            long posKey = pos.asLong();
            Long2BooleanMap currentMap = checkedPos;
            if (currentMap.containsKey(posKey)) {
                isVisible = currentMap.get(posKey);
            } else {
                // ---射线检测---
                // 缓存失效后进行射线检测
                isVisible = super.isVisible(blockEntity, cameraPos, 0.1);
                currentMap.put(posKey, isVisible);
            }
            if (!isVisible) return;
        }

        // ---6. 渲染执行---
        poseStack.pushPose();
        if (!empty) {
            renderItems(blockEntity, partialTick, poseStack, combinedLightIn, combinedOverlayIn,
                    renderState, collector, cameraState);
        } else {
            renderBlockModel(blockEntity, poseStack, collector, combinedLightIn, combinedOverlayIn);
        }
        poseStack.popPose();
    }

    /**
     * 获取要渲染的物品数组。
     * 子类需要实现此方法以提供具体的物品。
     * @param blockEntity 方块实体
     * @return 物品数组
     */
    protected abstract ItemStack[] getItems(T blockEntity);

    /**
     * 获取整体渲染偏移量。
     * 子类可以根据方块朝向等因素提供偏移。
     * @param blockEntity 方块实体
     * @return 偏移量
     */
    protected abstract Vector3f getRenderOffset(T blockEntity);

    /**
     * 应用基于方块的旋转。
     * 子类需要实现此方法以根据方块的朝向进行旋转。
     * @param blockEntity 方块实体
     * @param poseStack PoseStack
     */
    protected abstract void applyRotation(T blockEntity, PoseStack poseStack);

    @Override
    public boolean shouldRenderOffScreen() {
        return false;
    }

    protected void renderItems(@NotNull T blockEntity, float partialTick, @NotNull PoseStack poseStack, int combinedLightIn, int combinedOverlayIn,
                               @NotNull LootRenderState renderState, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraState) {
        ItemStack[] items = getItems(blockEntity);
        if (items == null || items.length == 0) { // 照理不应该发生，除非hasItem有问题
            renderBlockModel(blockEntity, poseStack, collector, combinedLightIn, combinedOverlayIn);
            return;
        }

        Vector3f renderOffset = getRenderOffset(blockEntity);
        Level level = blockEntity.getLevel();

        // 对时间取模防止 float 精度丢失 (不然没有上下浮动)
        float animationTime;
        if (level != null) {
            animationTime = (level.getGameTime() + partialTick) % 24000F;
        } else {
            animationTime = partialTick;
        }

        // 初始平移到方块中心
        poseStack.translate(0.5F + renderOffset.x(), renderOffset.y(), 0.5F + renderOffset.z());
        applyRotation(blockEntity, poseStack);

        // 应用全局等比缩放
        float scale = ITEM_RENDER_SCALE;
        poseStack.scale(scale, scale, scale);

        // 预计算公共动画参数
        float baseRotation = DO_SPINNING ? animationTime * SPIN_SPEED : 0.0F;
        boolean hasPhase = BOB_PHASE != 0.0F;
        // (Mth.sin + 1.0F) * 0.5F 值域为 [0, BOB_HEIGHT]
        // 初始高度就是最低点，物品只会向上浮动
        float staticBob = (!hasPhase && DO_BOBBING)
                ? (Mth.sin(animationTime * BOB_SPEED) + 1.0F) * 0.5F * BOB_HEIGHT
                : 0.0F;

        int baseSeed = (int) blockEntity.getBlockPos().asLong();

        int j = HashCommon.long2int(blockEntity.getBlockPos().asLong());
        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if (itemStack.isEmpty()) {
                continue;
            }
//            ItemStackRenderState itemStackRenderState = renderState.items[i];
//            if (itemStackRenderState == null) {
//                continue;
//            }

            poseStack.pushPose();

            // 布局位移 (4x4 矩阵排列)
            float xOffset = (i % 4 - 1.5F) * 0.5F;
            float zOffset = ((i % 16) / 4 - 1.5F) * 0.5F;
            float yOffset = (i / 16) * 0.5F;

            // 浮动值计算 (始终 >= 0)
            float bobEffect = (hasPhase && DO_BOBBING)
                    // 有相位差：每个物品独立计算 sin
                    ? (Mth.sin((animationTime + i * BOB_PHASE) * BOB_SPEED) + 1.0F) * 0.5F * BOB_HEIGHT
                    // 共振模式：使用循环外算好的值
                    : staticBob;

            // yOffset: 容器内物品堆叠的偏移
            // ITEM_RENDER_HEIGHT: 离地距离 (0 为紧贴地面)
            // bobEffect: 动态动画产生的向上位移 (始终 >= 0)
            // GROUND 模式下模型重心在 0.125 处，加上 VANILLA_GROUND_OFFSET 之后，y=0 恰好是模型底部边缘
            float finalY = yOffset + ITEM_RENDER_HEIGHT + bobEffect + RenderConfigTag.VANILLA_GROUND_OFFSET;

            poseStack.translate(xOffset, finalY, zOffset);

            // 应用自转
            if (DO_SPINNING) {
                poseStack.mulPose(Axis.YP.rotationDegrees(baseRotation));
            }

            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(itemStackRenderState,
                    itemStack,
                    ItemDisplayContext.GROUND,
                    blockEntity.getLevel(),
                    null,
                    baseSeed + i); // 掉落物渲染的随机种子
            itemStackRenderState.submit(poseStack, collector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    /*
     * net.minecraft.client.renderer.blockentity.ShelfRenderer
     * 用的时候创建一个就好了, 为啥原版书架要先检查并创建, 之后渲染的时候再判断一遍?
     * 渲染之前应该是有调用extractRenderState的, 其他不清楚
     */

    @Override
    public void extractRenderState(@NotNull T blockEntity, @NotNull LootRenderState renderState, float partialTick, @NotNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
//        ItemStack[] items = getItems(blockEntity);
//        int i = HashCommon.long2int(blockEntity.getBlockPos().asLong());
//        for (int j = 0; j < items.length; j++) {
//            ItemStack itemStack = items[j];
//            if (itemStack.isEmpty()) {
//                continue;
//            }
//            ItemStackRenderState itemstackrenderstate = new ItemStackRenderState();
//            this.itemModelResolver.updateForTopItem(itemstackrenderstate, itemStack, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, i + j); // 这个ItemOwner有什么用? seed又是干嘛的?
//            renderState.items[j] = itemstackrenderstate;
//        }
    }

//    private void submitItem(ShelfRenderState shelfRenderState, ItemStackRenderState itemStackRenderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, int index, float rotation) {
//        float f = (float)(index - 1) * 0.3125F;
//        Vec3 vec3 = new Vec3((double)f, shelfRenderState.alignToBottom ? (double)-0.25F : (double)0.0F, (double)-0.25F);
//        poseStack.pushPose();
//        poseStack.translate(0.5F, 0.5F, 0.5F);
//        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
//        poseStack.translate(vec3);
//        poseStack.scale(0.25F, 0.25F, 0.25F);
//        AABB aabb = itemStackRenderState.getModelBoundingBox();
//        double d0 = -aabb.minY;
//        if (!shelfRenderState.alignToBottom) {
//            d0 += -(aabb.maxY - aabb.minY) / (double)2.0F;
//        }
//
//        poseStack.translate((double)0.0F, d0, (double)0.0F);
//        itemStackRenderState.submit(poseStack, nodeCollector, shelfRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
//        poseStack.popPose();
//    }
}