package xiao.battleroyale.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import java.util.List;
import java.util.UUID; // 确保 UUID 导入正确

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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LootSpawnerBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LootSpawnerBlockEntity lootSpawnerBlockEntity) {
                if (player.isCreative() && player.isCrouching()) { // 切换物资刷新配置
                    int currentConfigId = lootSpawnerBlockEntity.getConfigId();
                    List<LootConfig> allConfigs = LootConfigManager.get().getAllLootSpawnerConfigs();

                    if (allConfigs.isEmpty()) { // 没有配置可切换，提示后直接返回
                        player.sendSystemMessage(Component.translatable("battleroyale.message.no_loot_configs_available"));
                        return InteractionResult.SUCCESS;
                    }
                    LootConfig nextConfig = allConfigs.get(0);
                    for (LootConfig config : allConfigs) { // 找第一个 ID 大于当前 ID 的配置
                        if (config.getId() > currentConfigId) {
                            nextConfig = config;
                            break;
                        }
                    }
                    lootSpawnerBlockEntity.setConfigId(nextConfig.getId()); // 设置配置文件的实际 ID
                    player.sendSystemMessage(Component.translatable("battleroyale.message.loot_config_switched", nextConfig.getId(), nextConfig.getName()));
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