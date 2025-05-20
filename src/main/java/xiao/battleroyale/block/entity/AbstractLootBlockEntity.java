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
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootObject;
import java.util.UUID;

public abstract class AbstractLootBlockEntity extends BlockEntity implements ILootObject {
    private static final String GAME_ID_TAG = "GameId";
    private static final String CONFIG_ID_TAG = "ConfigId";

    @Nullable
    private UUID gameId = null;
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
    public UUID getGameId() {
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
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID(GAME_ID_TAG)) {
            this.gameId = tag.getUUID(GAME_ID_TAG);
        } else {
            this.gameId = null; // 如果 NBT 中没有，则保持为 null，后续逻辑可能需要处理
        }
        if (tag.contains(CONFIG_ID_TAG, Tag.TAG_INT)) {
            this.configId = tag.getInt(CONFIG_ID_TAG);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (gameId != null) {
            tag.putUUID(GAME_ID_TAG, gameId);
        }
        tag.putInt(CONFIG_ID_TAG, this.configId);
    }
}