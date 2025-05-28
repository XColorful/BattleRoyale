package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
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
    }
}