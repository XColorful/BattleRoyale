package xiao.battleroyale.inventory;

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
        super(TYPE, id, playerInventory, blockEntity, blockEntity, 2); // 将 blockEntity 同时作为 lootContainer 传递
        this.blockEntity = blockEntity;
    }

    public LootSpawnerMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, null); // 用于客户端侧的空构造函数，可能不需要
    }
}