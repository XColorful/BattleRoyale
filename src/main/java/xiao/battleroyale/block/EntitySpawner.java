package xiao.battleroyale.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer; // 需要导入 ServerPlayer 以便 NetworkHooks.openScreen
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks; // 需要导入 NetworkHooks 以便打开界面
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig; // 需要导入 LootConfig，因为它在 use 方法中被使用

import java.util.List;

public class EntitySpawner extends AbstractLootBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public EntitySpawner() {
        super();
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)); // 设置默认朝向
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EntitySpawnerBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof EntitySpawnerBlockEntity entitySpawnerBlockEntity) {
                if (player.isCreative() && player.isCrouching()) { // 切换实体刷新配置
                    int currentConfigId = entitySpawnerBlockEntity.getConfigId();
                    List<LootConfig> allConfigs = LootConfigManager.get().getAllEntitySpawnerConfigs();

                    if (allConfigs.isEmpty()) { // 没有配置可切换，提示后直接返回
                        player.sendSystemMessage(Component.translatable("battleroyale.message.no_entity_configs_available"));
                        return InteractionResult.SUCCESS;
                    }
                    LootConfig nextConfig = allConfigs.get(0);
                    for (LootConfig config : allConfigs) { // 找第一个 ID 大于当前 ID 的配置
                        if (config.getId() > currentConfigId) {
                            nextConfig = config;
                            break;
                        }
                    }
                    entitySpawnerBlockEntity.setConfigId(nextConfig.getId()); // 设置配置文件的实际 ID
                    player.sendSystemMessage(Component.translatable("battleroyale.message.entity_config_switched", nextConfig.getId(), nextConfig.getName()));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}