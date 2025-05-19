package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.init.ModBlocks;
import xiao.battleroyale.inventory.LootSpawnerMenu;
import xiao.battleroyale.api.loot.ILootEntry;

import javax.annotation.Nullable;
import java.util.Random;

public class LootSpawnerBlockEntity extends AbstractLootContainerBlockEntity implements MenuProvider {
    public static final BlockEntityType<LootSpawnerBlockEntity> TYPE = BlockEntityType.Builder.of(LootSpawnerBlockEntity::new,
            ModBlocks.LOOT_SPAWNER.get()
    ).build(null);

    private final Random random = new Random();
    private boolean hasGeneratedLoot = false;
    private ResourceLocation lootObjectId;
    private int configId;

    public LootSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState, 18);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (!this.hasGeneratedLoot) {
                populateLoot(player);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                openContainer(serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
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

    private void populateLoot(Player player) {
        this.items.clear(); // 使用父类的 items 列表
        ResourceLocation lootObjectId = this.getLootObjectId();
        int configId = this.getConfigId();
        LootConfigManager lootConfigManager = LootConfigManager.get();
        LootConfig config = lootConfigManager.getLootSpawnerConfig(configId);

        if (lootObjectId != null && config != null) {
            ILootEntry<?> entry = config.getEntry();
            LootGenerator.generateLoot(player.level(), entry, this, player.getRandom()); // 直接使用 this 作为 Container
        }
        this.hasGeneratedLoot = true;
        this.setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.hasGeneratedLoot = tag.getBoolean("HasGenerated");
        if (tag.contains("lootObjectId")) {
            this.lootObjectId = ResourceLocation.tryParse(tag.getString("lootObjectId"));
        }
        this.configId = tag.getInt("configId");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("HasGenerated", this.hasGeneratedLoot);
        if (this.lootObjectId != null) {
            tag.putString("lootObjectId", this.lootObjectId.toString());
        }
        tag.putInt("configId", this.configId);
    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return !this.isRemoved(); // 检查方块实体是否仍然存在
    }
}