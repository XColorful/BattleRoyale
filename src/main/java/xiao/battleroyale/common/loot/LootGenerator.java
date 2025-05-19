package xiao.battleroyale.common.loot;

import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import xiao.battleroyale.api.loot.ILootEntry;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

public class LootGenerator {

    public static void generateLoot(Level level, ILootEntry<?> entry, Container container, RandomSource random) {
        //  将 RandomSource 转换为 Supplier<Float>
        Supplier<Float> randomFloatSupplier = () -> random.nextFloat();

        List<?> generatedLoot = entry.generateLoot(randomFloatSupplier);

        if (generatedLoot != null) {
            for (int i = 0; i < generatedLoot.size() && i < container.getContainerSize(); i++) {
                Object lootItem = generatedLoot.get(i);
                if (lootItem instanceof ItemStack) {
                    container.setItem(i, (ItemStack) lootItem);
                }
                //  如果你的 ILootEntry 可以生成其他类型的战利品 (例如 Entity)，
                //  你需要在这里添加相应的处理逻辑。
            }
        }
    }
}