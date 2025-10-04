package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import xiao.battleroyale.block.AbstractLootBlock;
import xiao.battleroyale.block.LootSpawner;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;

public class LootSpawnerRenderer extends LootContainerRenderer<LootSpawnerBlockEntity> {

    public LootSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ItemStack[] getItems(LootSpawnerBlockEntity blockEntity) {
        ItemStack[] items = new ItemStack[blockEntity.getContainerSize()];
        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            items[i] = blockEntity.getItem(i);
        }
        return items;
    }

    @Override
    protected Vector3f getRenderOffset(LootSpawnerBlockEntity blockEntity) {
        return new Vector3f(0, 0, 0);
    }

    @Override
    protected void applyRotation(LootSpawnerBlockEntity blockEntity, PoseStack poseStack) {
        BlockState blockState = blockEntity.getBlockState();
        LootSpawner block = (LootSpawner) blockState.getBlock();
        Direction facing = blockState.getValue(block.getFacingProperty());

        // 获取旋转角度
        float yRot = 0;
        if (facing == Direction.SOUTH) {
            yRot = 180;
        } else if (facing == Direction.EAST) {
            yRot = 90;
        } else if (facing == Direction.WEST) {
            yRot = 270;
        }
        // 对于 NORTH 默认就是 0 度，无需额外设置

        // 应用 Y 轴旋转
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
    }
}