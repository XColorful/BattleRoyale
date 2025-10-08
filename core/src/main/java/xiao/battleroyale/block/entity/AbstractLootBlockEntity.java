package xiao.battleroyale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootObject;
import xiao.battleroyale.api.loot.LootNBTTag;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.util.TagUtils;

import java.util.UUID;

public abstract class AbstractLootBlockEntity extends BlockEntity implements ILootObject {

    @Nullable
    protected UUID gameId = null;
    protected int configId = LootConfigManager.get().getDefaultConfigId();

    protected AbstractLootBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public abstract int getConfigFolderId();

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
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider p_329179_) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, p_329179_);
        saveAdditional(output);
        return output.buildResult();
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.gameId = TagUtils.getUUID(input, LootNBTTag.GAME_ID_TAG);
        this.configId = TagUtils.getInt(input, LootNBTTag.CONFIG_ID_TAG, this.configId);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        if (gameId != null) {
            TagUtils.putUUID(output, LootNBTTag.GAME_ID_TAG, gameId);
        }
        TagUtils.putInt(output, LootNBTTag.CONFIG_ID_TAG, this.configId);
    }
}