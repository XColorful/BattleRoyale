package xiao.battleroyale.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.api.loot.ILootEntry;

public class LootSpawnerMenu extends AbstractLootMenu {
    public static final MenuType<LootSpawnerMenu> TYPE = IForgeMenuType.create((windowId, inv, data) -> {
        LootSpawnerBlockEntity blockEntity = (LootSpawnerBlockEntity) inv.player.level().getBlockEntity(data.readBlockPos());
        return new LootSpawnerMenu(windowId, inv, blockEntity);
    });

    private final LootSpawnerBlockEntity blockEntity;

    public LootSpawnerMenu(int id, Inventory playerInventory, LootSpawnerBlockEntity blockEntity) {
        super(TYPE, id, playerInventory, new SimpleContainer(18), blockEntity, 2);
        this.blockEntity = blockEntity;
        if (!playerInventory.player.level().isClientSide()) {
            populateLoot(playerInventory.player);
        }
    }

    @Override
    protected void populateLoot(Player player) {
        this.lootContainer.clearContent();
        ResourceLocation lootObjectId = this.blockEntity.getLootObjectId();
        int configId = this.blockEntity.getConfigId();
        LootConfigManager lootConfigManager = LootConfigManager.get();
        LootConfig config = lootConfigManager.getLootSpawnerConfig(configId);

        if (lootObjectId != null && config != null) {
            ILootEntry<?> entry = config.getEntry();
            LootGenerator.generateLoot(player.level(), entry, this.lootContainer, player.getRandom());
        }
    }
}