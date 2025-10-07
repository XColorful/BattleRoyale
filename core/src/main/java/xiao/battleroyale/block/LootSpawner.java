package xiao.battleroyale.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.network.NetworkHook;
import xiao.battleroyale.util.ChatUtils;

import java.util.List;

public class LootSpawner extends AbstractLootBlock {
    public static final MapCodec<LootSpawner> CODEC = simpleCodec(LootSpawner::new);

    private static final EnumProperty<Direction> THIS_FACING = EnumProperty.create("facing", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    private static final VoxelShape THIS_SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public LootSpawner(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override public EnumProperty<Direction> getFacingProperty() {
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
        return new LootSpawnerBlockEntity(pos, state);
    }

    @Override
    public @NotNull InteractionResult useLootBlock(@NotNull BlockState pState, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LootSpawnerBlockEntity lootSpawnerBlockEntity) {
                if (player.isCreative() && player.isCrouching()) { // 切换物资刷新配置
                    int currentConfigId = lootSpawnerBlockEntity.getConfigId();
                    List<LootConfig> allConfigs = LootConfigManager.get().getConfigEntryList(LootConfigTypeEnum.LOOT_SPAWNER);
                    if (allConfigs == null || allConfigs.isEmpty()) {
                        ChatUtils.sendMessageToPlayer((ServerPlayer) player, Component.translatable("battleroyale.message.no_loot_spawner_config_available"));
                        return InteractionResult.SUCCESS;
                    }

                    LootConfig nextConfig = allConfigs.getFirst();
                    for (LootConfig config : allConfigs) {
                        if (config.getConfigId() > currentConfigId) {
                            nextConfig = config;
                            break;
                        }
                    }
                    lootSpawnerBlockEntity.setConfigId(nextConfig.getConfigId());
                    ChatUtils.sendMessageToPlayer((ServerPlayer) player, Component.translatable("battleroyale.message.loot_spawner_lootid_switched", nextConfig.getConfigId(), nextConfig.name));
                    return InteractionResult.SUCCESS;
                } else { // 打开界面
                    NetworkHook.get().openScreen((ServerPlayer) player, lootSpawnerBlockEntity, (buf) -> {
                        buf.writeBlockPos(pos); // 传递方块的位置
                        buf.writeUtf(lootSpawnerBlockEntity.getGameId() != null ? lootSpawnerBlockEntity.getGameId().toString() : "");
                    });
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.CONSUME;
    }
}