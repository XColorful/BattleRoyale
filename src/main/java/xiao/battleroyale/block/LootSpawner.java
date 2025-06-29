package xiao.battleroyale.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import java.util.List;

public class LootSpawner extends AbstractLootBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public LootSpawner() {
        super();
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new LootSpawnerBlockEntity(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LootSpawnerBlockEntity lootSpawnerBlockEntity) {
                if (player.isCreative() && player.isCrouching()) { // 切换物资刷新配置
                    int currentConfigId = lootSpawnerBlockEntity.getConfigId();
                    List<LootConfig> allConfigs = LootConfigManager.get().getLootSpawnerConfigList();
                    if (allConfigs.isEmpty()) {
                        player.sendSystemMessage(Component.translatable("battleroyale.message.no_loot_spawner_config_available"));
                        return InteractionResult.SUCCESS;
                    }

                    LootConfig nextConfig = allConfigs.get(0);
                    for (LootConfig config : allConfigs) {
                        if (config.getConfigId() > currentConfigId) {
                            nextConfig = config;
                            break;
                        }
                    }
                    lootSpawnerBlockEntity.setConfigId(nextConfig.getConfigId());
                    player.sendSystemMessage(Component.translatable("battleroyale.message.loot_spawner_lootid_switched", nextConfig.getConfigId(), nextConfig.name));
                    return InteractionResult.SUCCESS;
                } else { // 打开界面
                    NetworkHooks.openScreen((ServerPlayer) player, lootSpawnerBlockEntity, (buf) -> {
                        buf.writeBlockPos(pos); // 传递方块的位置
                        buf.writeUtf(lootSpawnerBlockEntity.getGameId() != null ? lootSpawnerBlockEntity.getGameId().toString() : "");
                    });
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}