package xiao.battleroyale.common.loot.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.item.IItemLootData;
import xiao.battleroyale.api.minecraft.ComponentsTag;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.Optional;

public class ItemData implements IItemLootData {
    private final @Nullable Item item;
    private final @NotNull CompoundTag nbt;
    private final int count;
    private final @NotNull CompoundTag itemTag;
    private static final String EMPTY_RL = "minecraft:air";
    private static final String EMPTY_TYPE = "air";
    private final boolean isEmpty;

    public ItemData(@NotNull String rl, @NotNull CompoundTag nbt, int count) {
        this.item = BattleRoyale.getMcRegistry().getItem(BattleRoyale.getMcRegistry().createResourceLocation(rl));
        this.isEmpty = this.item == null
                || (this.item.toString().equals(EMPTY_TYPE) && !rl.equals(EMPTY_RL));
        if (this.item == null) {
            BattleRoyale.LOGGER.warn("Faild to get item type from ResourceLocation {}", rl);
        }
        this.nbt = nbt;
        this.count = count; // 原版已经处理小于等于0
        this.itemTag = calculateItemTag(rl);
    }

    @Nullable
    @Override
    public ItemStack getItemStack(LootGenerator.LootContext lootContext) {
        if (this.isEmpty()) {
            return null;
        }
        Optional<ItemStack> itemStack = ItemStack.parse(lootContext.serverLevel.registryAccess(), this.itemTag);
        return itemStack.orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean isEmpty() {
        return this.isEmpty;
    }

    private CompoundTag calculateItemTag(String rl) {
        CompoundTag itemTag = new CompoundTag();
        ItemStack itemStack = this.item != null ? new ItemStack(this.item, this.count) : ItemStack.EMPTY;
        itemTag.putString("id", rl);
        itemTag.putInt("count", itemStack.getCount());
        CompoundTag components = (CompoundTag) this.nbt.get(ComponentsTag.COMPONENTS);
        if (components != null) { // components抽离出来，其他放到minecraft:custom_data下
            // 去掉components
            this.nbt.remove(ComponentsTag.COMPONENTS);
            // 逐个移到minecraft:custom_data里
            CompoundTag custom_data = new CompoundTag();
            for (String key : this.nbt.getAllKeys()) {
                custom_data.put(key, this.nbt.get(key));
            }
            // 把minecraft:custom_data放到components里
            components.put(ComponentsTag.CUSTOM_DATA, custom_data);
            // 把components放到itemTag里
            itemTag.put(ComponentsTag.COMPONENTS, components);
        } else { // 全部放到components:{"minecraft:custom_data":{}}
            components = new CompoundTag();
            components.put(ComponentsTag.CUSTOM_DATA, this.nbt);
            itemTag.put(ComponentsTag.COMPONENTS, components);
        }
        return itemTag;
    }
}
