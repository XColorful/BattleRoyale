package xiao.battleroyale.common.loot.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.item.IItemLootData;

public class ItemData implements IItemLootData {
    private @Nullable Item item;
    private @Nullable CompoundTag nbt;
    private int count;

    public ItemData(String rl, @Nullable String nbt, int count) {
        this.item = BuiltInRegistries.ITEM.get(new ResourceLocation(rl));
        if (this.item == null) {
            BattleRoyale.LOGGER.warn("无法找到 ResourceLocation {} 物品类型", rl);
        }
        if (nbt != null) {
            try {
                this.nbt = TagParser.parseTag(nbt);
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("解析物品NBT失败 {}: {}", rl, e.getMessage());
            }
        }
        this.count = count;
    }

    @Nullable
    @Override
    public ItemStack getItemStack() {
        if (this.item == null) {
            return null;
        }
        ItemStack itemStack = new ItemStack(this.item, this.count);
        if (this.nbt != null) {
            itemStack.setTag(this.nbt);
        }
        return itemStack;
    }
}
