package xiao.battleroyale.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;

public class LootSpawnerMenu extends AbstractLootMenu {

    private final LootSpawnerBlockEntity blockEntity;

    public LootSpawnerMenu(MenuType<LootSpawnerMenu> type, int id, Inventory playerInventory, LootSpawnerBlockEntity blockEntity) {
        super(type, id, playerInventory, blockEntity, blockEntity);
        this.blockEntity = blockEntity;
    }

    public LootSpawnerMenu(int id, Inventory playerInventory) {
        this(null, id, playerInventory, null);
    }

    @Override
    protected void addLootContainer(Container lootContainer) {
        super.addLootContainer(lootContainer, 27);
    }

    @Override
    protected void addPlayerInventory(Inventory playerInventory) {
        super.addPlayerInventory(playerInventory, 76);
    }
}