package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.init.ModBlocks;
import xiao.battleroyale.inventory.LootSpawnerMenu;
import net.minecraft.core.NonNullList;
import java.util.UUID;
import javax.annotation.Nullable;
import java.util.Random;

public class LootSpawnerBlockEntity extends AbstractLootContainerBlockEntity implements MenuProvider {

    private final Random random = new Random();
    private int configId;

    public LootSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.LOOT_SPAWNER_BE.get(), pos, blockState, 18); // 使用 RegistryObject 获取 BlockEntityType
    }

    @Override
    public UUID getGameId() {
        return super.getGameId();
    }

    @Override
    public void setGameId(UUID gameId) {
        super.setGameId(gameId);
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    private void openContainer(ServerPlayer player) {
        player.openMenu(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.battleroyale.loot_spawner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new LootSpawnerMenu(id, playerInventory, this);
    }

    public int getConfigId() {
        return this.configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
        this.setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.configId = tag.getInt("configId");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("configId", this.configId);
    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return !this.isRemoved();
    }
}