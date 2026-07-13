package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.block.entity.AbstractLootBlockEntity;
import xiao.battleroyale.client.renderer.BlockModelRenderer;

public abstract class AbstractBlockRenderer<T extends AbstractLootBlockEntity, S extends BlockEntityRenderState> implements BlockEntityRenderer<T, S> {

    protected final BlockModelResolver blockModelResolver;
    protected final ModelBlockRenderer modelBlockRenderer;

    public AbstractBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
        this.modelBlockRenderer = new ModelBlockRenderer(
                Minecraft.getInstance().options.ambientOcclusion().get(),
                true,
                Minecraft.getInstance().getBlockColors()
        );
    }

    protected void renderBlockModel(@NotNull BlockEntity blockEntity, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockState = blockEntity.getBlockState();
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BlockStateModel blockStateModel = modelManager.getBlockStateModelSet().get(blockState);

        BlockModelRenderer.get().renderBlockModel(
                blockState,
                blockStateModel,
                this.modelBlockRenderer,
                poseStack,
                collector,
                combinedLightIn,
                combinedOverlayIn
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull S createRenderState() {
        return (S) new BlockEntityRenderState();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void submit(@NotNull S renderState, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraState) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        T blockEntity = (T) minecraft.level.getBlockEntity(renderState.blockPos);
        if (blockEntity == null) return;

        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        int combinedLightIn = renderState.lightCoords;

        int progress = renderState.breakProgress != null ? renderState.breakProgress.progress() : -1;
        int combinedOverlayIn = progress != -1 ? getDestroyProgressOverlay(progress) : OverlayTexture.NO_OVERLAY;

        this.render(blockEntity, partialTick, poseStack, combinedLightIn, combinedOverlayIn, renderState, collector, cameraState);
    }

    protected abstract void render(@NotNull T blockEntity, float partialTick, @NotNull PoseStack poseStack, int combinedLightIn, int combinedOverlayIn,
                                   @NotNull S renderState, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraState);

    public static int getDestroyProgressOverlay(int progress) {
        if (progress < 0 || progress > 9) {
            return 0; // -1 或其他无效进度返回 0
        }
        // 破坏进度 progress (0-9) 被编码到 combinedOverlayIn 的 S 坐标 (U)
        // 公式是 (progress * 20 + 10) / 256.0 * 65536
        // 实际上就是 (progress * 20 + 10) << 16，因为 65536 = 256 << 8 * 256 << 8
        // 简化后：
        return (progress * 20 + 10) << 16;
    }

    /**
     * 通用射线检测逻辑
     * @param blockEntity 目标方块实体
     * @param cameraPos 摄像机实时坐标 (解决 F5 穿帮)
     * @param yOffset 目标点 Y 轴和向内的偏移（用于微调检测高度）
     */
    protected boolean isVisible(T blockEntity, Vec3 cameraPos, double yOffset) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return false;

        BlockPos pos = blockEntity.getBlockPos();
        double bx = pos.getX();
        double by = pos.getY() + yOffset;
        double bz = pos.getZ();

        // 1. 中心点
        if (isPointVisible(mc, cameraPos, new Vec3(bx + 0.5, by, bz + 0.5), pos)) return true;

        // 计算坐标缩进
        double min = yOffset;
        double max = 1.0 - yOffset;

        // 判断玩家所处方位
        boolean isEast = cameraPos.x > (bx + 0.5);
        boolean isSouth = cameraPos.z > (bz + 0.5);

        // 确立四个角的优先级坐标
        double closeX = isEast ? bx + max : bx + min;
        double farX = isEast ? bx + min : bx + max;
        double closeZ = isSouth ? bz + max : bz + min;
        double farZ = isSouth ? bz + min : bz + max;

        // 2. 最近角 (象限交点)
        if (isPointVisible(mc, cameraPos, new Vec3(closeX, by, closeZ), pos)) return true;

        // 3. 左侧角 & 4. 右侧角 (主轴方向两侧)
        if (isPointVisible(mc, cameraPos, new Vec3(closeX, by, farZ), pos)) return true;
        if (isPointVisible(mc, cameraPos, new Vec3(farX, by, closeZ), pos)) return true;

        // 5. 最远角
        if (isPointVisible(mc, cameraPos, new Vec3(farX, by, farZ), pos)) return true;

        return false;
    }

    protected boolean isPointVisible(Minecraft mc, Vec3 cameraPos, Vec3 targetPos, BlockPos targetBlockPos) {
        BlockHitResult result = mc.level.clip(new ClipContext(
                cameraPos,
                targetPos,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                mc.getCameraEntity()
        ));
        return result.getType() == HitResult.Type.MISS || result.getBlockPos().equals(targetBlockPos);
    }
}
