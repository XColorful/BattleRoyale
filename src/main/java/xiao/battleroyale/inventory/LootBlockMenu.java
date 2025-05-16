package xiao.battleroyale.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.jetbrains.annotations.Nullable;

public class LootBlockMenu extends AbstractContainerMenu {
    public static final MenuType<LootBlockMenu> TYPE = IForgeMenuType.create((windowId, inv, data) -> {
        ResourceLocation blockId = data.readResourceLocation();
        return new LootBlockMenu(windowId, inv, blockId);
    });

    private final ResourceLocation blockId;

    public LootBlockMenu(int id, Inventory inventory, @Nullable ResourceLocation resourceLocation) {
        super(TYPE, id);
        this.blockId = resourceLocation;
    }

    @Nullable
    public ResourceLocation getBlockId() {
        return blockId;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }
}
