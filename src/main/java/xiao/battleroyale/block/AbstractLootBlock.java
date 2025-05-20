package xiao.battleroyale.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.DefaultAssets;
import xiao.battleroyale.api.item.builder.BlockItemBuilder;
import xiao.battleroyale.api.item.nbt.BlockItemDataAccessor;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import java.util.UUID;

public class AbstractLootBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

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

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LootSpawnerBlockEntity lootSpawnerBlockEntity && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, lootSpawnerBlockEntity, (buf) -> {
                    UUID gameId = lootSpawnerBlockEntity.getGameId();
                    buf.writeUtf(gameId == null ? DefaultAssets.DEFAULT_BLOCK_ID.toString() : gameId.toString());
                });
            }
            return InteractionResult.CONSUME;
        }
    }

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
            if (blockentity instanceof LootSpawnerBlockEntity e) {
                if (stack.getItem() instanceof BlockItemDataAccessor accessor) {
                    UUID gameId = accessor.getBlockGameId(stack);
                    e.setGameId(gameId);
                    e.setConfigId(0); // 默认 configId
                } else {
                    e.setGameId(UUID.randomUUID()); // 或者其他合适的默认 UUID 生成逻辑
                    e.setConfigId(0); // 默认 configId
                }
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof LootSpawnerBlockEntity e) {
            if (e.getGameId() != null) {
                return BlockItemBuilder.create(this)
                        .withNBT(nbt -> nbt.putUUID("BlockGameId", e.getGameId())) // 存储 GameId 到物品 NBT
                        .build();
            }
            return new ItemStack(this);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    public float parseRotation(Direction direction) {
        return 90.0F * (3 - direction.get2DDataValue()) - 90;
    }
}