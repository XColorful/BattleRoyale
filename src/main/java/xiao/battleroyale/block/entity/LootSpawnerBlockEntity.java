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
import net.minecraft.world.level.block.state.BlockState;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.init.ModBlocks;
import xiao.battleroyale.inventory.LootSpawnerMenu;
import net.minecraft.core.NonNullList;

import javax.annotation.Nullable;
import java.util.Random;

public class LootSpawnerBlockEntity extends AbstractLootContainerBlockEntity implements MenuProvider {

    private final Random random = new Random();
    private ResourceLocation lootObjectId;
    private int configId;

    public LootSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.LOOT_SPAWNER_BE.get(), pos, blockState, 18); // 使用 RegistryObject 获取 BlockEntityType
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
        BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} loading data...", this.worldPosition);
        super.load(tag);
        BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} finished loading super data.", this.worldPosition);

        if (tag.contains("lootObjectId")) {
            this.lootObjectId = ResourceLocation.tryParse(tag.getString("lootObjectId"));
            BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} loaded lootObjectId: {}", this.worldPosition, this.lootObjectId);
        } else {
            BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} has no lootObjectId in tag.", this.worldPosition);
        }
        this.configId = tag.getInt("configId");
        BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} loaded configId: {}", this.worldPosition, this.configId);

        for (int i = 0; i < this.items.size(); i++) {
            if (!this.items.get(i).isEmpty()) {
                BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} loaded item in slot {}: {}", this.worldPosition, i, this.items.get(i));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} saving data...", this.worldPosition);
        super.saveAdditional(tag);
        BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} finished saving super data.", this.worldPosition);

        if (this.lootObjectId != null) {
            tag.putString("lootObjectId", this.lootObjectId.toString());
            BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} saved lootObjectId: {}", this.worldPosition, this.lootObjectId);
        } else {
            BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} has null lootObjectId, not saving.", this.worldPosition);
        }
        tag.putInt("configId", this.configId);
        BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} saved configId: {}", this.worldPosition, this.configId);

        for (int i = 0; i < this.items.size(); i++) {
            if (!this.items.get(i).isEmpty()) {
                BattleRoyale.LOGGER.info("LootSpawnerBlockEntity at {} saving item in slot {}: {}", this.worldPosition, i, this.items.get(i));
            }
        }
    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return !this.isRemoved();
    }
}