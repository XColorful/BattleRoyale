package xiao.battleroyale.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.DefaultAssets;
import xiao.battleroyale.api.item.builder.BlockItemBuilder;
import xiao.battleroyale.api.item.nbt.BlockItemDataAccessor;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;

public class AbstractLootBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AbstractLootBlock() {
        super(Properties.of().sound(SoundType.WOOD).strength(2.5F, 2.5F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LootSpawnerBlockEntity lootSpawnerBlockEntity && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, lootSpawnerBlockEntity, (buf) -> {
                    ResourceLocation rl = lootSpawnerBlockEntity.getLootObjectId() == null ? DefaultAssets.DEFAULT_BLOCK_ID : lootSpawnerBlockEntity.getLootObjectId();
                    buf.writeResourceLocation(rl);
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
                    ResourceLocation id = accessor.getBlockId(stack);
                    e.setLootObjectId(id);
                    e.setConfigId(0); // 默认 configId
                } else {
                    e.setLootObjectId(DefaultAssets.DEFAULT_BLOCK_ID);
                    e.setConfigId(0); // 默认 configId
                }
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof LootSpawnerBlockEntity e) {
            if (e.getLootObjectId() != null) {
                return BlockItemBuilder.create(this).setId(e.getLootObjectId()).build();
            }
            return new ItemStack(this);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    public float parseRotation(Direction direction) {
        return 90.0F * (3 - direction.get2DDataValue()) - 90;
    }
}