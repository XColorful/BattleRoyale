package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.init.ModBlocks;
import xiao.battleroyale.inventory.LootBlockMenu;

import javax.annotation.Nullable;

public class LootSpawnerBlockEntity extends AbstractLootBlockEntity implements MenuProvider {
    public static final BlockEntityType<LootSpawnerBlockEntity> TYPE = BlockEntityType.Builder.of(LootSpawnerBlockEntity::new,
            ModBlocks.LOOT_SPAWNER.get()
    ).build(null);

    public LootSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Loot Spawner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new LootBlockMenu(id, inventory, getLootObjectId());
    }
}