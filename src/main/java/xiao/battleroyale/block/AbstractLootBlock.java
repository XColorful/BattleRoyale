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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.item.builder.BlockItemBuilder;
import xiao.battleroyale.api.loot.LootNBT;
import xiao.battleroyale.block.entity.AbstractLootBlockEntity;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;

import java.util.UUID;

public abstract class AbstractLootBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public AbstractLootBlock() {
        super(Properties.of()
                .sound(SoundType.WOOD)
                .strength(2.5F, 2.5F)
                .noOcclusion()
                .noCollission()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    @Override
    public abstract InteractionResult use(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return null; // 由子类实现
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide) {
            BlockEntity blockentity = world.getBlockEntity(pos);
            if (blockentity instanceof AbstractLootBlockEntity e) {
                CompoundTag nbt = stack.getTag();

                // GameId
                UUID gameId = null;
                if (nbt != null && nbt.hasUUID(LootNBT.GAME_ID_TAG)) {
                    gameId = nbt.getUUID(LootNBT.GAME_ID_TAG);
                }
                e.setGameId(gameId != null ? gameId : UUID.randomUUID());

                // ConfigId
                int configId = LootConfigManager.DEFAULT_CONFIG_ID;
                if (nbt != null && nbt.contains(LootNBT.CONFIG_ID_TAG, Tag.TAG_INT)) {
                    configId = nbt.getInt(LootNBT.CONFIG_ID_TAG);
                }
                e.setConfigId(configId);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof LootSpawnerBlockEntity e) {
            UUID gameId = e.getGameId();
            int configId = e.getConfigId();

            return BlockItemBuilder.create(this)
                    .withNBT(nbt -> {
                        if (gameId != null) {
                            nbt.putUUID(LootNBT.GAME_ID_TAG, gameId);
                        } else {
                            nbt.remove(LootNBT.GAME_ID_TAG);
                        }
                        nbt.putInt(LootNBT.CONFIG_ID_TAG, configId);
                    })
                    .build();
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    public float parseRotation(Direction direction) {
        return 90.0F * (3 - direction.get2DDataValue()) - 90;
    }
}