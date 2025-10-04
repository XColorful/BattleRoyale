package xiao.battleroyale.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;

import java.util.List;

public class EntitySpawner extends AbstractLootBlock {
    public static final MapCodec<EntitySpawner> CODEC = simpleCodec(EntitySpawner::new);

    private static final DirectionProperty THIS_FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    private static final VoxelShape THIS_SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    public EntitySpawner(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override public DirectionProperty getFacingProperty() {
        return THIS_FACING;
    }
    @Override public VoxelShape getBlockShape() {
        return THIS_SHAPE;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(THIS_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EntitySpawnerBlockEntity(pos, state);
    }

    @Override
    public @NotNull ItemInteractionResult useLootBlock(@NotNull BlockState pState, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof EntitySpawnerBlockEntity entitySpawnerBlockEntity) {
                if (player.isCreative() && player.isCrouching()) { // 切换实体刷新配置
                    int currentConfigId = entitySpawnerBlockEntity.getConfigId();
                    List<LootConfig> allConfigs = LootConfigManager.get().getConfigEntryList(LootConfigTypeEnum.ENTITY_SPAWNER);
                    if (allConfigs == null || allConfigs.isEmpty()) {
                        player.sendSystemMessage(Component.translatable("battleroyale.message.no_entity_spawner_config_available"));
                        return ItemInteractionResult.SUCCESS;
                    }

                    LootConfig nextConfig = allConfigs.getFirst();
                    for (LootConfig config : allConfigs) {
                        if (config.getConfigId() > currentConfigId) {
                            nextConfig = config;
                            break;
                        }
                    }
                    entitySpawnerBlockEntity.setConfigId(nextConfig.getConfigId());
                    player.sendSystemMessage(Component.translatable("battleroyale.message.entity_spawner_lootid_switched", nextConfig.getConfigId(), nextConfig.name));
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        // return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        // ↑会导致右键功能连续触发两次
        return ItemInteractionResult.CONSUME;
    }
}