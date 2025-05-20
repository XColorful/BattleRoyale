package xiao.battleroyale.common.loot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootEntry;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import xiao.battleroyale.api.loot.ILootObject;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import java.util.UUID;

public class LootGenerator {

    public static void generateLoot(Level level, ILootEntry<?> entry, Object target, RandomSource random) {
        Supplier<Float> randomFloatSupplier = () -> random.nextFloat();
        List<?> generatedLoot = entry.generateLoot(randomFloatSupplier);

        if (generatedLoot == null) {
            return;
        }
        if (target instanceof Container container) {
            container.clearContent();
            for (int i = 0; i < generatedLoot.size() && i < container.getContainerSize(); i++) {
                Object lootItem = generatedLoot.get(i);
                if (lootItem instanceof ItemStack itemStack) {
                    container.setItem(i, itemStack);
                } else {
                    BattleRoyale.LOGGER.warn("ignore non-item loot for container loot block");
                }
            }
        } else {
            // 实体生成
        }
    }

    public static void refreshLootBlock(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ILootObject lootObject)) {
            return;
        }
        UUID gameId = lootObject.getGameId();
        int configId = lootObject.getConfigId();
        LootConfigManager.LootConfig config = LootConfigManager.get().getLootConfig(blockEntity, configId);

        if (gameId != null && config != null) {
            ILootEntry<?> entry = config.getEntry();
            LootGenerator.generateLoot(level, entry, blockEntity, player.getRandom());
            blockEntity.setChanged();
        }
    }
}