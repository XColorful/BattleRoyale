package xiao.battleroyale.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import net.minecraft.core.BlockPos;

public class LootSpawnerMenu extends AbstractLootMenu {
    public static final MenuType<LootSpawnerMenu> TYPE = IForgeMenuType.create((windowId, inv, extraData) -> {
        BlockPos pos = extraData.readBlockPos();
        LootSpawnerBlockEntity blockEntity = (LootSpawnerBlockEntity) inv.player.level().getBlockEntity(pos);
        return new LootSpawnerMenu(windowId, inv, blockEntity);
    });

    private final LootSpawnerBlockEntity blockEntity;

    public LootSpawnerMenu(int id, Inventory playerInventory, LootSpawnerBlockEntity blockEntity) {
        super(TYPE, id, playerInventory, blockEntity, blockEntity);
        this.blockEntity = blockEntity;
    }

    public LootSpawnerMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, null); // 用于客户端侧的空构造函数，可能不需要
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