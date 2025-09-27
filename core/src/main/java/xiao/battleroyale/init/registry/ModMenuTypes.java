package xiao.battleroyale.init.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.registry.IMenuTypeFactory;
import xiao.battleroyale.api.init.registry.IRegistrar;
import xiao.battleroyale.api.init.registry.IRegistryObject;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.inventory.LootSpawnerMenu;

public class ModMenuTypes {

    private static final IMenuTypeFactory MENU_FACTORY =
            BattleRoyale.getRegistrarFactory().getMenuTypeFactory();

    public static final IRegistrar<MenuType<?>> MENU_TYPES =
            BattleRoyale.getRegistrarFactory().createMenuTypes(BattleRoyale.MOD_ID);

    public static final IRegistryObject<MenuType<LootSpawnerMenu>> LOOT_SPAWNER_MENU =
            MENU_TYPES.register("loot_spawner_menu", () ->
                    MENU_FACTORY.createBlockEntityMenu((windowId, inv, extraData) -> {
                        BlockPos pos = extraData.readBlockPos();
                        LootSpawnerBlockEntity blockEntity = (LootSpawnerBlockEntity) inv.player.level().getBlockEntity(pos);
                        return new LootSpawnerMenu(null, windowId, inv, blockEntity);
                    })
            );
}