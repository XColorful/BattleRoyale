package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootObject;
import xiao.battleroyale.api.loot.LootNBTTag;
import xiao.battleroyale.config.common.loot.LootConfigManager;

import java.util.UUID;

public abstract class AbstractLootBlockEntity extends BlockEntity implements ILootObject {

    @Nullable
    protected UUID gameId = null;
    protected int configId = LootConfigManager.get().getDefaultConfigId();

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
    public @Nullable UUID getGameId() {
        return this.gameId;
    }

    @Override
    public void setGameId(UUID gameId) {
        this.gameId = gameId;
        this.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID(LootNBTTag.GAME_ID_TAG)) {
            this.gameId = tag.getUUID(LootNBTTag.GAME_ID_TAG);
        } else {
            this.gameId = null;
        }
        if (tag.contains(LootNBTTag.CONFIG_ID_TAG, Tag.TAG_INT)) {
            this.configId = tag.getInt(LootNBTTag.CONFIG_ID_TAG);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (gameId != null) {
            tag.putUUID(LootNBTTag.GAME_ID_TAG, gameId);
        }
        tag.putInt(LootNBTTag.CONFIG_ID_TAG, this.configId);
    }
}