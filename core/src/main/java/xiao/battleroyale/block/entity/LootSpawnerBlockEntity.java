package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.init.registry.ModBlocks;
import xiao.battleroyale.init.registry.ModMenuTypes;
import xiao.battleroyale.inventory.LootSpawnerMenu;

import javax.annotation.Nullable;

public class LootSpawnerBlockEntity extends AbstractLootContainerBlockEntity implements MenuProvider {

    protected static final AABB RELATIVE_RENDER_AABB = new AABB(0, 0, 0, 1, 0.5, 1); // 半格高

    public LootSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.LOOT_SPAWNER_BE.get(), pos, blockState, 18);
    }

    public static int getLootContainerSize() {
        return 18;
    }

    @Override
    public int getConfigFolderId() {
        return LootConfigTypeEnum.LOOT_SPAWNER;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.battleroyale.loot_spawner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new LootSpawnerMenu(ModMenuTypes.LOOT_SPAWNER_MENU.get(), id, playerInventory, this);
    }

    @Override
    public boolean stillValid(@NotNull Player p_18946_) {
        return !this.isRemoved();
    }

    // 1.21.1neoforge默认管线已经做了优化
//    @Override
    public AABB getRenderBoundingBox() {
        return RELATIVE_RENDER_AABB.move(this.worldPosition);
    }
}