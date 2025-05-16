package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.DefaultAssets;
import xiao.battleroyale.init.ModBlocks;
import xiao.battleroyale.inventory.LootBlockMenu;

public class LootBlockEntity extends BlockEntity implements MenuProvider {
    public static final BlockEntityType<LootBlockEntity> TYPE = BlockEntityType.Builder.of(LootBlockEntity::new,
            ModBlocks.LOOT_SPAWNER.get()
    ).build(null);

    private static final String ID_TAG = "BlockId";

    @Nullable
    private ResourceLocation id = null;

    public LootBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    @Nullable
    public ResourceLocation getId() {
        return id;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(this.worldPosition);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Loot Block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new LootBlockMenu(id, inventory, getId());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ID_TAG, Tag.TAG_STRING)) {
            this.id = ResourceLocation.tryParse(tag.getString(ID_TAG));
        } else {
            this.id = DefaultAssets.DEFAULT_BLOCK_ID;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (id != null) {
            tag.putString(ID_TAG, id.toString());
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
