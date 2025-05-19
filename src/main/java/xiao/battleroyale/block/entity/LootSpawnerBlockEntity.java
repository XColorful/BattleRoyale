package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.init.ModBlocks;
import xiao.battleroyale.inventory.LootSpawnerMenu;
import net.minecraft.core.NonNullList;

import javax.annotation.Nullable;
import java.util.Random;

public class LootSpawnerBlockEntity extends AbstractLootContainerBlockEntity implements MenuProvider {
    public static final BlockEntityType<LootSpawnerBlockEntity> TYPE = BlockEntityType.Builder.of(LootSpawnerBlockEntity::new,
            ModBlocks.LOOT_SPAWNER.get()
    ).build(null);

    private final Random random = new Random();
    private ResourceLocation lootObjectId;
    private int configId;

    public LootSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState, 18);
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

    public void setLootObjectId(ResourceLocation lootObjectId) {
        this.lootObjectId = lootObjectId;
        this.setChanged();
    }

    public ResourceLocation getLootObjectId() {
        return this.lootObjectId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
        this.setChanged();
    }

    public int getConfigId() {
        return this.configId;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("lootObjectId")) {
            this.lootObjectId = ResourceLocation.tryParse(tag.getString("lootObjectId"));
        }
        this.configId = tag.getInt("configId");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.lootObjectId != null) {
            tag.putString("lootObjectId", this.lootObjectId.toString());
        }
        tag.putInt("configId", this.configId);
    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return !this.isRemoved();
    }
}