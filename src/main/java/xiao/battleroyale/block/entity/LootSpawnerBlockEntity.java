package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.init.ModBlocks;
import xiao.battleroyale.init.ModMenuTypes;
import xiao.battleroyale.inventory.LootSpawnerMenu;
import net.minecraft.core.NonNullList;
import javax.annotation.Nullable;

public class LootSpawnerBlockEntity extends AbstractLootContainerBlockEntity implements MenuProvider {

    public LootSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.LOOT_SPAWNER_BE.get(), pos, blockState, 18);
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
}