package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.DefaultAssets;
import xiao.battleroyale.api.loot.ILootObject;

public abstract class AbstractLootBlockEntity extends BlockEntity implements ILootObject {
    private static final String ID_TAG = "LootId";
    private static final String CONFIG_ID_TAG = "ConfigId";

    @Nullable
    private ResourceLocation lootObjectId = null;
    private int configId = 0;

    protected AbstractLootBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public int getConfigId() {
        return this.configId;
    }

    @Override
    public void setConfigId(int configId) {
        this.configId = configId;
        this.setChanged();
    }

    @Override
    public ResourceLocation getLootObjectId() {
        return this.lootObjectId;
    }

    @Override
    public void setLootObjectId(ResourceLocation lootObjectId) {
        this.lootObjectId = lootObjectId;
        this.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ID_TAG, Tag.TAG_STRING)) {
            this.lootObjectId = ResourceLocation.tryParse(tag.getString(ID_TAG));
        } else {
            this.lootObjectId = DefaultAssets.DEFAULT_BLOCK_ID;
        }
        if (tag.contains(CONFIG_ID_TAG, Tag.TAG_INT)) {
            this.configId = tag.getInt(CONFIG_ID_TAG);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (lootObjectId != null) {
            tag.putString(ID_TAG, lootObjectId.toString());
        }
        tag.putInt(CONFIG_ID_TAG, this.configId);
    }
}