package xiao.battleroyale.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.item.builder.BlockItemBuilder;
import xiao.battleroyale.api.loot.LootNBTTag;
import xiao.battleroyale.block.entity.AbstractLootBlockEntity;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;

import java.util.UUID;

public abstract class AbstractLootBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public AbstractLootBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return null;
    }

    @Override
    public abstract @NotNull InteractionResult use(@NotNull BlockState pState, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return null; // 由子类实现
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide) {
            BlockEntity blockentity = world.getBlockEntity(pos);
            if (blockentity instanceof AbstractLootBlockEntity e) {
                CompoundTag nbt = stack.getTag();

                // GameId
                UUID gameId = null;
                if (nbt != null && nbt.hasUUID(LootNBTTag.GAME_ID_TAG)) {
                    gameId = nbt.getUUID(LootNBTTag.GAME_ID_TAG);
                }
                e.setGameId(gameId != null ? gameId : UUID.randomUUID());

                // ConfigId
                int configId = LootConfigManager.get().getDefaultConfigId(); // 默认获取loot_spawner的默认id
                if (nbt != null && nbt.contains(LootNBTTag.CONFIG_ID_TAG, Tag.TAG_INT)) {
                    configId = nbt.getInt(LootNBTTag.CONFIG_ID_TAG);
                }
                e.setConfigId(configId);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos pos, BlockState state) {
        BlockEntity blockentity = levelReader.getBlockEntity(pos);
        if (blockentity instanceof LootSpawnerBlockEntity e) {
            UUID gameId = e.getGameId();
            int configId = e.getConfigId();

            return BlockItemBuilder.create(this)
                    .withNBT(nbt -> {
                        if (gameId != null) {
                            nbt.putUUID(LootNBTTag.GAME_ID_TAG, gameId);
                        } else {
                            nbt.remove(LootNBTTag.GAME_ID_TAG);
                        }
                        nbt.putInt(LootNBTTag.CONFIG_ID_TAG, configId);
                    })
                    .build();
        }
        return super.getCloneItemStack(levelReader, pos, state);
    }

    public float parseRotation(Direction direction) {
        return 90.0F * (3 - direction.get2DDataValue()) - 90;
    }
}